
import matplotlib.path as mplPath
import numpy as np

import gpx
import strava

class Gps_points:

	def __init__(self, gravel_file="gravel_points.gpx", pavement_file="Asfalt1.gpx", pavement_file_poly="Asfalt_Oslo_poly.gpx"):
		self._gravel_points = self.fetch_points(gravel_file)
		self._pavement_points = self.fetch_points(pavement_file)
		self._pavement_poly = self.fetch_poly(pavement_file_poly)

		self._gravel_points_np = np.array([ [p.latitude, p.longitude] for p in self._gravel_points ])
		self._pavement_points_np = np.array([ [p.latitude, p.longitude] for p in self._pavement_points ])

	def __str__(self):
		print(f"Gravel points: {len(self._gravel_points)}\nPavement points: {len(self._pavement_points)}")

	def fetch_points(self, file_name):
		points = gpx.read_gpx(file_name)
		#points = gpx.drop_duplicates_points(points)
		points = gpx.add_extra_points(points, max_dist=500)


		return points

	def fetch_poly(self, file_name):
		points = gpx.read_gpx(file_name)
		poly = mplPath.Path(np.array([ [p.latitude, p.longitude] for p in points ]))

		return poly

	def get_poly(self):
		return self._pavement_poly

	def get_pavement_points_np(self):
		return self._pavement_points_np

	def get_gravel_points_np(self):
		return self._gravel_points_np







