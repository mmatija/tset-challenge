## Setup instructions

In order to run the project, you need to have **Java 24** installed.
Before you run the app or tests, you need to set up the database and create the required tables.
You can do this by starting a Postgres docker container using the provided `docker-compose.yaml` file.

```shell
docker compose -f ./database/docker-compose.yaml up -d
```

You can optionally specify username, password, database name and port using `DATABASE_USER`, `DATABASE_PASSWORD`, `DATABASE_NAME` and `DATABASE_PORT` environment variables respectively.
This will allow you to run multiple instances of the database with different configurations if needed, for example when running tests and the application at the same time.

```shell
DATABASE_USER=tset DATABASE_PASSWORD=tset DATABASE_NAME=tset_challenge DATABASE_PORT=5433 docker compose -f ./database/docker-compose.yaml up
```

> _NOTE: Make sure to override the application defaults in that case using the same environment variables_

To run the tests, execute the `test` gradle task using the provided `gradlew` executable.

```shell
./gradlew test
```

To start the server, run `bootRun` gradle task.

```shell
./gradlew bootRun
```
By default, the server will start on port `8080`, which you can change by setting the `SERVER_PORT` environment variable.
Once the server is running, you can create a new deployment by sending a POST request to `/deploy` endpoint:

```shell
curl "localhost:8080/deploy" -H "Content-Type: application/json" --data '{"name":"Service A","version": 1}' --request POST
```

After that, you can fetch all deployments by sending a GET request to `/services` endpoint:

```shell
curl "localhost:8080/services?systemVersion=1"
```


## Design considerations

### Data duplication as opposed to data normalization

Currently, when new system version is created, relationship to latest deployed services is achieved by duplicating the data from the previous version.
For example, if system version `n` has 100 deployed services and one of those services releases a new version, those 100 services will be duplicated to the new system version `n+1`.

This simplifies the logic of fetching deployed services using a simple `WHERE` clause as opposed to building a result set using loops/joins/unions/etc.
This decision was rationalized by the fact that we don't expect the number of deployed services to be extremely high and that we would not be releasing new versions extremely often.

Let's say we have 100 services and we release a new version of all of them every day. That would mean we would add `100*100 = 10000` new rows every day.
Every row contains a service name (`VARCHAR`), service version (`INTEGER`) and system version (`INTEGER`).

If we do some napkin math and say that each service name is ~50 characters long we get 50 + 4 + 4 = 58 bytes per row. That means we would be adding around `58*10000 = 580000` bytes or ~0.55 MiB of data every day. Even if that number is 10 times higher, we would still be adding only ~5.5 MiB of data every day, it would be ~2GiB in a year.
On top of that, we could archive or delete old data if needed.

### Moving business logic to database repository layer

The decision to move parts of business logic to the repository layer allowed me to greatly simplify the design of creating new system versions and linking with existing service versions. It allowed me to make all changes in a single transaction and avoid potential issues with concurrency and data consistency.

It also allowed me to design `POST /deploy` endpoint to be idempotent, without the need for error handling and retry logic in case of concurrent requests.

### Not adding pagination

Following the same rationale as with data duplication, we don't expect to have a very high number of deployed services at any given time, so we can afford to fetch all of them without pagination and still have good performance.
