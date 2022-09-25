import gpxpy
from geopy import distance
import pandas as pd

def read_gpx(file_name):
    file = open(file_name, 'r')
    gpx_data = gpxpy.parse(file)

    return gpx_data.tracks[0].segments[0].points

def save_gpx_points(points, file_name="gravel.gpx"):
    gpx = gpxpy.gpx.GPX()

    # Create first track in our GPX:
    gpx_track = gpxpy.gpx.GPXTrack()
    gpx.tracks.append(gpx_track)

    # Create first segment in our GPX track:
    gpx_segment = gpxpy.gpx.GPXTrackSegment()
    gpx_track.segments.append(gpx_segment)
    
    gpx_segment.points = points
    
    file = open(file_name, 'w')
    file.write(gpx.to_xml())
    file.close()

def merge_gpx(file_name1, file_name2, new_name=None):
    file1 = open(file_name1, 'r')
    gpx_data1 = gpxpy.parse(file1)
    
    file2 = open(file_name2, 'r')
    gpx_data2 = gpxpy.parse(file2)

    gpx_points = gpx_data1.tracks[0].segments[0].points
    
    gpx_points = gpx_points + gpx_data2.tracks[0].segments[0].points
    
    if new_name == None:
        save_gpx_points(gpx_points, file_name=f"{file_name1[:-3]}_&_{file_name2[:-3]}.gpx")
    else:
        save_gpx_points(gpx_points, file_name=new_name)

def get_dist(a, b):
    return distance.great_circle((a.latitude, a.longitude), (b.latitude, b.longitude)).m

def make_dist_list(points, max_dist=150.0):
    dist = []
    for n in range(0, len(points) - 1):
        start = points[n]
        end = points[n+1]
        im = get_dist(start, end)
        
        if im > max_dist:
            im = 0.0
        dist.append(im)
        
    dist.append(0.0)
    return dist

def drop_duplicates_points(points):
    df = pd.DataFrame(columns=['lon', 'lat', 'elev'])
    for point in points:
        df = df.append({'lon': point.longitude, 'lat': point.latitude, 'elev': point.elevation}, ignore_index=True)
        
    df = df.drop_duplicates(subset=None, keep='first')
    
    points = []
    for val in df.values:
        points.append(gpxpy.gpx.GPXTrackPoint(val[0], val[1], elevation=val[2]))
    
    return points


def add_points(points, dist, threshold=30.0, max_dist=150.0):
    index_point = 0
    for i in range(0, len(dist)):
        if dist[i] > threshold and dist[i] < max_dist:
            new_lat = (points[index_point].latitude + points[index_point + 1].latitude) / 2
            new_lon = (points[index_point].longitude + points[index_point + 1].longitude) / 2
            new_elev = (points[index_point].elevation + points[index_point + 1].elevation) / 2
            new_point = gpxpy.gpx.GPXTrackPoint(new_lat, new_lon, elevation=new_elev)
            index_point += 1
            points.insert(index_point, new_point)

        index_point += 1

    return points

def add_extra_points(points, threshold=30.0, max_dist=150.0):
    dist = make_dist_list(points)
    num_last = len(points)
    while max(dist) > threshold:
        points = add_points(points, dist, threshold, max_dist)
        dist = make_dist_list(points, max_dist)
        #print(f"Added {len(points) - num_last} points\t Total: {len(points)}\t Max dist: {max(dist)}")
    
    return points







