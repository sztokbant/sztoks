**About**

Sztoks is a balance sheet-like web app which allows one to keep a monthly snapshot of their finances, including *assets*, *liabilities*, *income received*, *investment transactions*, as well as *donations* made.

Many financially successful people emphasize the importance of regularly *saving* a portion of one's income, as well as *donating* to charitable causes. Sztoks has built-in mechanisms that allow one to easily *"set the sail"* and visualize their progress on saving and on givivng to charity. Knowing where one stands is key to getting where one wants to get, in finances or anything else.

**Development**

Sztoks was built using Spring Boot and Java technology with a web front-end.

To run from IntelliJ IDEA, create a Gradle run configuration, set *Tasks* as `:bootRun`, and set the following "Environment variables" in order to point to your MySQL server settings, e.g.:

```
JDBC_DATABASE_URL=jdbc:mysql://localhost/myequity?user=root&password=&reconnect=true&serverTimezone=UTC;JDBC_DATABASE_PASSWORD=;JDBC_DATABASE_USERNAME=root
```
