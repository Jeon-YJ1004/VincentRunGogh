from pySpark.RouteTotalDistance import calculate_total_distance

def calculate_center_and_distance(route):
    # route가 객체가 아닌 dict 형태이므로 each["lat"]으로 접근
    lats = [each["lat"] for each in route.positionList]
    lngs = [each["lng"] for each in route.positionList]


    center_lat = sum(lats)/len(lats)
    center_lng = sum(lngs)/len(lngs)

    route_list = []
    for i in range(len(lats)):
        route_list.append((lats[i],lngs[i]))

    distance = calculate_total_distance(route_list)
    return center_lat, center_lng, distance