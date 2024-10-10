from pySpark.KdTreeMatchingRoute import matching_user_path_to_roads
from pySpark.PedestrianRoad import road_data_processing
from models import Position

def generate_route(user_drawn_path,leftLat, leftLng, rightLat, rightLng):
    nearest_roads_df = road_data_processing(leftLat,leftLng,rightLat,rightLng,  "Daejeon")
    route_coords = matching_user_path_to_roads(user_drawn_path, nearest_roads_df)
    position_list = [Position(lat=coord[0], lng=coord[1]) for coord in route_coords]
    return position_list