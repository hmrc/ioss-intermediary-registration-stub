
# ioss-intermediary-registration-stub

This is the repository for Import One Stop Shop Intermediary Registration Stub, it is used by the Import One Stop Shop Intermediary Registration Frontend.


Frontend: https://github.com/hmrc/ioss-intermediary-registration-frontend

Backend: https://github.com/hmrc/ioss-intermediary-registration

## Run the application

To update from Nexus and start all services from the RELEASE version instead of snapshot
```
sm --start IMPORT_ONE_STOP_SHOP_ALL
```

### To run the application locally execute the following:
```
sm --stop IOSS_INTERMEDIARY_REGISTRATION_STUB
```
and
```
sbt 'run 10186'
```

Unit Tests
------------

To run the unit tests you will need to open an sbt session on the browser.

To run all tests, run the following command in your sbt session:
```
test
```

To run a single test, run the following command in your sbt session:
```
testOnly <package>.<SpecName>
```

An asterisk can be used as a wildcard character without having to enter the package, as per the example below:
```
testOnly *<SpecName>
```

Requirements
------------

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE] to run.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").