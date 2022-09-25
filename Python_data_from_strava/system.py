
import pandas as pd
import numpy as np
from geopy import distance
import time
from datetime import datetime, timedelta
import sys

import gpx 
import strava
import data_classes

def get_dist_array(point, array):
	ret = []
	for p in array:
		ret.append(distance.great_circle((point[0], point[1]), (p[0], p[1])).m)
	return ret



def calc_typer(route, data, threshold=30.0):
	route_np = route

	classification = []

	for point in route_np:

		if data.get_poly().contains_point((point[0], point[1])):
			classification.append("asfalt")
			continue


		euc_dist = np.sum((data.get_pavement_points_np() - point)**2, axis=1)
		dist = get_dist_array(point, data.get_pavement_points_np()[euc_dist.argsort()][:2,:])
			
		if max(dist) < threshold:
			classification.append('asfalt')
			continue

		euc_dist = np.sum((data.get_gravel_points_np() - point)**2, axis=1)
		dist = get_dist_array(point, data.get_gravel_points_np()[euc_dist.argsort()][:2,:])
			
		if max(dist) < threshold:
			classification.append('grus')
		else:
			classification.append('terreng')

	return classification

def calculate_totals(classification: list):

	totals = {}

	for typ in set(clas):
		indices = np.where(np.array(clas) == typ)
		dist = np.sum(t_distance_diff[indices])
		sec = indices[0].size

		totals[typ] = {"time": str(timedelta(seconds=sec)), "distance": dist / 1000}

	return totals

if __name__ == "__main__":

	print("Fetching training...", end="\r")
	start = time.time()
	strava_con = strava.Strava_connection()
	activity_id = strava.get_activity_id(strava_con.access_token(), datetime.strptime(sys.argv[1], "%Y-%m-%d"))
	t_points = strava.get_data_from_strava(strava_con.access_token(), activity_id, typ="latlng")
	t_distance = strava.get_data_from_strava(strava_con.access_token(), activity_id, typ="distance")
	t_distance_diff = np.ediff1d(np.array(t_distance))
	t_distance_diff = np.insert(t_distance_diff, 0, 0)
	print(f"Done fetching training ({time.time() - start: .2f}s )")

	print("Initialising data...", end="\r")
	start = time.time()
	data = data_classes.Gps_points()
	print(f"Done initialising data ({time.time() - start: .2f}s )")

	print("Calculating types...", end="\r")
	start = time.time()
	clas = calc_typer(t_points, data)
	print(f"Done calculating types ({time.time() - start: .2f}s )")


	print("Calculating totals...", end="\r")
	start = time.time()
	totals = calculate_totals(clas)
	print(f"Done calculating totals ({time.time() - start: .2f}s )\n")


	for typ in totals.keys():
		print(f"--- {typ} ---")
		print(f"Time: {totals[typ]['time']}")
		print(f"Distance: {totals[typ]['distance']: .2f} km")
		print("\n")

	# t_df = pd.DataFrame(columns=['lon', 'lat'])
	# for point in t_points:
	# 	t_df = t_df.append({'lon': point[1], 'lat': point[0]}, ignore_index=True)
	# t_df['typer'] = clas

	# fig = px.scatter_mapbox(t_df, lat='lat', lon='lon', color="typer", mapbox_style="open-street-map", zoom=11)
	# fig.show()




