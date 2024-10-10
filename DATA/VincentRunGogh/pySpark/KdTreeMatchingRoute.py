from scipy.spatial import KDTree
import networkx as nx

from pySpark.RouteTotalDistance import haversine

def matching_user_path_to_roads(user_drawn_path, pedestrian_df):

    # kdTree 이용해서 보행자 도로와 매핑시키기
    # pedestrian_df에서 데이터 가져오기
    pedestrian_data = pedestrian_df.select("way_id", "node_id", "lat", "lng").collect()
    # KD트리, 배열
    node_coords = []
    nodes = {}
    ways = {}

    # 노드와 웨이 데이터를 수집
    G = nx.Graph()  # 도로 네트워크 그래프 생성
    # 쿼리에서 가져온 도로 데이터들
    for row in pedestrian_data:
        node_id = row['node_id']
        lat = row['lat']
        lng = row['lng']
        # dict 형태로 노드 정보 저장하기
        nodes[node_id] = (lat,lng)
        # kd-tree에 사용할 노드 좌표 저장
        node_coords.append((lat,lng))
        G.add_node(node_id, pos=(lat, lng))

    # kd-tree 이용해서 얻은 좌표를 통해 노드간 연결 그래프로 추가
    for row in pedestrian_data:
        way_id = row['way_id']
        node_id = row['node_id']

        # way_id별 node_id 리스트 형태로 저장해두기
        if way_id not in ways:
            ways[way_id] = []

        ways[way_id].append(node_id)

    # print(ways)
    # 각 way_id에 대해 노드 리스트 연결
    for way_id, node_list in ways.items():
        print("check ", way_id, node_list)
        for i in range(len(node_list) - 1):
            G.add_edge(node_list[i], node_list[i + 1])
            if not G.has_edge(node_list[i], node_list[i + 1]):
                print(f"엣지 추가 실패: {node_list[i]}와 {node_list[i + 1]} 연결되지 않음")

    # print(G.nodes())
    # print(G.edges())
    # kdtree 생성
    kdtree = KDTree(node_coords)

    # 사용자가 그린 경로 좌표 (latList, lngList)
    # leaflet에서 좌표 받아온 것 -> redis에 저장된 상태
    # 받아오기
    # 2. 사용자가 그린 그림의 좌표 획득 (request 요청한 매개변수)

    # 3. 각 그림 좌표와 가장 가까운 도로 노드 찾기 및 경로 연결성 확인
    matched_nodes = []
    matched_nodes_set = set()  # 중복 체크를 위한 집합

    for coord in user_drawn_path:
        # 1. 가장 가깝다고 볼 수 있는 kd-tree 후보 기존 1개에서 3개로 뽑아줌
        # 2. 각각 하버사인 거리 계산하고 최소값 찾아냄
        # 3. 가장 최소값을 후보로 넣기
        # 50m를 유클리안 거리로 환산한 값 : 약 0.000716
        _, indexes = kdtree.query(coord, k=3, distance_upper_bound=0.000716)
        # print(indexes)
        # 0 1 2
        min_dist = int(1e6)
        if len(indexes) == 0: # 못찾으면 다음 거
            continue
        index = indexes[0]
        # print("계산전 :", index)
        for i in range(len(indexes)):
            try:
                candidate = node_coords[indexes[i]]
            except Exception as e:
                continue
            # 기존 값 계산 *3
            temp_distance = haversine(coord[0],coord[1],candidate[0],candidate[1])
            if temp_distance < min_dist:
                index = indexes[i]
                min_dist = temp_distance
        if min_dist == int(1e6):
            continue
        # print("k=3 거친 후 ", index)
        nearest_node = node_coords[index]

        node_id = list(nodes.keys())[list(nodes.values()).index(nearest_node)]
        # 같은 노드가 들어가지 않도록 중복값 제거
        if node_id not in matched_nodes_set:
            matched_nodes.append(node_id)
            matched_nodes_set.add(node_id)

    #print(matched_nodes)

    # 처음과 끝을 연결하지 않음
    #matched_nodes.append(matched_nodes[0])

    # 후처리
    # 도로와 연결되었는지 검증하기

    # final_matched_nodes = []
    # i = 0
    # while i < len(matched_nodes)-1:
    #     current_node = matched_nodes[i]
    #     flag = False
    #
    #     for j in range(i+1, len(matched_nodes)):
    #         next_node = matched_nodes[j]
    #         # 디버깅
    #         # if current_node in G.nodes() and next_node in G.nodes():
    #         #
    #         #     #print(f"노드 {current_node}와 {next_node}는 그래프에 존재합니다.")
    #         # else:
    #         #     #print(f"노드 {current_node} 또는 {next_node}는 그래프에 존재하지 않습니다.")
    #
    #         if G.has_edge(current_node, next_node):
    #             final_matched_nodes.append(current_node)
    #             i = j # 연결된 노드로 이동
    #             flag = True
    #             break
    #
    #     if not flag:
    #         print(f"노드 {current_node}의 연결노드를 찾을 수 없다")
    #         i += 1

    route_coords = [nodes[node_id] for node_id in matched_nodes]
    print("art, route node 개수 ",len(node_coords), len(matched_nodes),len(route_coords))
    #
    # distance_frechet = frdist(node_coords, route_coords)
    #
    # similarity = 1 / (1+distance_frechet)
    # print("fr 거리 유사도 {}%".format(similarity*100))

    return route_coords

# 추가 고도화 방안
# 1. 해당 아트 점마다 매칭되는 루트와 도로(way)가 있을텐데
# 2.
# 유사도 데이터 테스트
# frechet distance 구하기
