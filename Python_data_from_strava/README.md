
# Data from strava
Little program to make my training log easier to make. Fetches a gpx file from strava using strava API. Then calculates the time and distance covered in different terrain (gravel, trail or pavement). Not the fastest program, but it works, and its a lot faster than the manual method. Works only in northern Oslo and the south part of Nordmarka.

## Requirements
- requests
- geopy
- numpy
- pandas
- matplotlib

## Usage

Insert client secret and refresh token from strava. Tutorial to get this is here, https://developers.strava.com. Lines in code is 10 and 11 in `strava.py`. file

run
```BASH
Python3 system.py <YEAR>-<MONTH>-<DAY>
```