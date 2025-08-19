# Sztoks
_A Java Web implementation of Eduardo Sztokbant's Personal Finance Tracking Method_

## About

_Sztoks_ is a balance sheet-like web app which allows one to keep a monthly
snapshot of their finances.

_Sztoks_ features the ability to control multiple types of *assets* and
*liabilities*, as well as three types of transactions: *income received*,
*investing activities* and *donations* made.

Monthly snapshots can be generated with the click of a button. When a snapshot
is created, *assets* and *liabilities* are carried over to the new snapshot. As
for transactions, only those that are configured to be recurring are carried
over.

Many financially successful people emphasize the importance of regularly
*saving* a portion of one's income, as well as *making donations* to
charitable causes. _Sztoks_ has built-in mechanisms that allow one to easily
*"set the sail"* for their personal finances and visualize their progress at
saving and on giving to charity. Knowing where one stands is key to getting
where one wants to get, in finances or anything else.

At its current stage, _Sztoks_ is ready to be used both from desktop and
mobile browsers.

A running instance of _Sztoks_ can be found at www.sztoks.com. It is provided
for educational purposes only with no guarantees. Use it at your own discretion.

## License

_Sztoks_ is distributed under the MIT License (Ref.:
https://opensource.org/licenses/MIT). See the LICENSE.txt file for details.

## Development

_Sztoks_ is built using Spring Boot and Java technology with a web front-end.

To run from IntelliJ IDEA:

1. Start a local MySQL server (mysqld).
1. Connect to MySQL and create database `sztoks`.
1. On IntelliJ IDEA, create a new `Gradle` run configuration, set *Run* as
`:bootRun`, and set the following *Environment variables* in order to point to
your MySQL server settings, e.g.:

```
SZTOKS_ENV=localhost;JDBC_DATABASE_URL=jdbc:mysql://localhost/sztoks_dev?user=root&password=&reconnect=true&serverTimezone=UTC;JDBC_DATABASE_PASSWORD=;JDBC_DATABASE_USERNAME=root;SERVER_PORT=9090
```

## Contributing

Contributions are welcome and must adhere to the existing code style and test
coverage standards.
