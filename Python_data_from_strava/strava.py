import requests as r
import json
from datetime import datetime, timedelta
import time

class Strava_connection:

	def __init__(self):
		self._client_id = 88615
		self._client_secret = # INSERT CLIENT SECRET FROM STRAVA
		self._refresh_token = # INSERT REFRESH TOKEN FROM STRAVA
		self._access_token = None
		self._expires_at = 0

	def access_token(self):

		if self._expires_at < time.time() + 1.0:
			url = "https://www.strava.com/oauth/token"
			msg = {"client_id": self._client_id,
		   		   "client_secret": self._client_secret,
		   		   "refresh_token": self._refresh_token,
		   		   "grant_type": "refresh_token"}
    
			response = r.post(url, json=msg).json()

			self._access_token = response["access_token"]
			self._expires_at = response["expires_at"]

		return self._access_token

def get_new_access_token(refresh_token):
	url = "https://www.strava.com/oauth/token"
	msg = {"client_id": 88615,
		   "client_secret": "c4b8c3024d399027e5bd4c843fc9f289ebeab49b",
		   "refresh_token": refresh_token,
		   "grant_type": "refresh_token"}
    
	response = r.post(url, json=msg)
	return response.json()["access_token"]

def get_activity_id(access_token: str, date: datetime):
	start_date = (date).timestamp()
	end_date = (date + timedelta(days=1)).timestamp()

	url = "https://www.strava.com/api/v3/athlete/activities"
	header = {'Authorization': 'Bearer ' + access_token}
	params = {'before': end_date, 'after': start_date}

	result = r.get(url, headers=header, params=params)

	return result.json()[0]['id']


def get_df_from_strava(access_token: str, activity_id: int):
	url = f"https://www.strava.com/api/v3/activities/{activity_id}/streams"
	header = {'Authorization': 'Bearer ' + access_token}
	
	latlong = r.get(url, headers=header, params={'keys':['latlng']}).json()[0]['data']
	time_list = r.get(url, headers=header, params={'keys':['time']}).json()[1]['data']
	altitude = r.get(url, headers=header, params={'keys':['distance']}).json()[1]['data']
	
	data = pd.DataFrame([*latlong], columns=['lat','lon'])
	data['distance'] = altitude
	data['time'] = time_list
	
	return data

def get_data_from_strava(access_token: str, activity_id: int, typ: str):
	url = f"https://www.strava.com/api/v3/activities/{activity_id}/streams"
	header = {'Authorization': 'Bearer ' + access_token}
	
	data = r.get(url, headers=header, params={'keys':[typ]}).json()

	if typ == "latlng" or typ == "distance":
		return data[0]['data']
	else:
		return data[1]['data']
