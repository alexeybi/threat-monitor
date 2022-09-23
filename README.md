## Network Threat Monitor

This application allows monitoring outgoing requests over a wireless network in browser in near real-time
showing a threat type for each request.

### Threat analysis

The requests are checked with [Google web-risk api](https://cloud.google.com/web-risk) for several threat types:

* `SOCIAL_ENGINEERING` - Social engineering targeting any platform
* `MALWARE` - Malware targeting any platform
* `UNWANTED_SOFTWARE` - Unwanted software targeting any platform

### Requirements

*  `sudo tshark` is used for traffic capture, hence [tshark](https://tshark.dev/) has to be locally installed and given permission to run without requiring a password (e.g. by adding a rule to sudoers):
```
# /etc/sudoers.d/tshark-no-pass
%admin ALL=(ALL) NOPASSWD: <path-to-tshark-bin>
```
* [Web-risk is set up](https://cloud.google.com/web-risk/docs/detect-malicious-urls) and the `GOOGLE_APPLICATION_CREDENTIALS` env. variable that points to the json file with the configuration is available
* [Java(11+)](https://adoptium.net/en-GB/temurin/releases/?version=11)

### Running

* `sbt run` will start the application without packaging
* If packaged with `stage`: run the startup script in `$ ./taget/universal/stage/bin/server`
* Open `localhost:8080` in a browser

### Building and packaging

* Install Java(11+) and Scala(3.x), e.g. through [cs setup](https://get-coursier.io/docs/cli-installation)
* With `sbt` installed run in the project root: `sbt "clean;fullLinkJS;stage"` - this creates the package with all app mappings and executable `bash/bat` script
* Build artifacts are located in `/taget/universal/stage/`:
```bash
bin/
  server       <- startup bash script
  server.bat   <- cmd.exe script
  static/      <- static files
lib/
  <Dependencies jar files>
```

### Alternative packaging formats

It's possible to package the app in other formats, e.g.:

`sbt "clean;fullLinkJS;packageOsxDmg"` - creates `/target/universal/monitor-0.1.0.dmg` file

Package structure is the same as `stage`.
See [Universal Packager](https://sbt-native-packager.readthedocs.io/en/latest/formats/universal.html#build) docs for other possible formats.

### Tests

* To run all the tests: `sbt test`
* To run only server or client tests: `sbt server/test`, or `sbt client/test`

### Additional resources
[PhishTank](https://phishtank.org/phish_search.php?valid=y&active=All&Search=Search) lists verified phishing urls
which can be used to check threat monitoring.
Note that `WebRisk` might still consider some verified phishing urls as harmless.

### :exclamation: Important note on pricing and quotas :exclamation:

 * Web-risk is free to use for up to 100k calls per month with the request quota of 6k per minute for `SearchUris` requests.
 Please check the links for the up-to-date information on pricing and quotas:
	- [Pricing](https://cloud.google.com/web-risk/pricing/) 
	- [Quotas](https://cloud.google.com/web-risk/quotas)

---
Built with [fs2](https://fs2.io/), [http4s](https://http4s.org/), [cats-effect](https://typelevel.org/cats-effect/), and
[laminar](https://laminar.dev/)
