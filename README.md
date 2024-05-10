# Kostal Energy Parser

This parser periodically (every minute) fetches new data from the provided Kostal Inverter,
<br>writes the data to an Excel file and uploads the file once every day to the provided 
Synology DSM.

## Usage
Download the [latest release](https://github.com/lucasskywalker64/KostalEnergyParser/releases/latest)
and the env.properties [template](https://github.com/lucasskywalker64/KostalEnergyParser/blob/master/env.properties)
and fill in like shown below 
<br>and place it next to the jar file.

| host=https://example.diskstation.me |
|-------------------------------------|
| port=5001                           |
| username=user                       |
| password=passwd                     |
| uploadPath=/ExampleFolder           |
| bypass_ssl=true                     |
| hostInverter=192.168.1.111          |
| portInverter=1502                   |

note: the `bypass_ssl=true` option disables hostname verification so only set this to
<br> true on a local network that you trust.

## Built With

- [Java JDK 22](https://www.oracle.com/java/technologies/downloads/#java22) - Java™ Platform
- [Maven](https://maven.apache.org/) - Dependency Management

## License

This project is licensed under the GPL-3.0 License - see the [LICENSE](https://github.com/lucasskywalker64/KostalEnergyParser/blob/master/LICENSE)
file for details.