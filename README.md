## Network Threat Monitor

This application allows monitoring network requests in browser in near real-time
showing a threat type for each request.

### Threat analysis

The requests are checked with [Google web-risk api](https://cloud.google.com/web-risk) for several threat types:

* `SOCIAL_ENGINEERING` - Social engineering targeting any platform.
* `MALWARE` - Malware targeting any platform.
* `UNWANTED_SOFTWARE` - Unwanted software targeting any platform.

### Requirements

* [tshark](https://tshark.dev/) has to be locally installed and given permissions to run without requiring password (e.g. by adding a rule to sudoers): 
```
# /etc/sudoers.d/tshark-no-pass
%admin ALL=(ALL) NOPASSWD: <path-to-tshark-bin>
```
* [Web-risk is set up](https://cloud.google.com/web-risk/docs/detect-malicious-urls) and the `GOOGLE_APPLICATION_CREDENTIALS` env. variable that points to the json file with the configuration is available. 

### Running //TODO
```

```

### :exclamation: Important note on pricing and quotas :exclamation:

 * Web-risk is free to use for up to 100k calls per month with the request quota of 6k per minute for `SearchUris` requests.
 Please check the links for the up-to-date information on pricing and quotas:
	- [Pricing](https://cloud.google.com/web-risk/pricing/) 
	- [Quotas](https://cloud.google.com/web-risk/quotas)

---
Built with [fs2](https://fs2.io/), [http4s](https://http4s.org/), [cats-effect](https://typelevel.org/cats-effect/), and
[laminar](https://laminar.dev/)
