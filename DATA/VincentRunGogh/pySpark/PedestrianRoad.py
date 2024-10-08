import requests
from pyspark.sql import SparkSession
from pyspark.sql import types

def road_data_processing(left_lat, left_lng, right_lat, right_lng, region):
    spark = SparkSession.builder.appName("RoadDataProcessing").getOrCreate()

    # WARN 로그 추가
    spark.sparkContext.setLogLevel("WARN")

    # 도로 데이터 설정하기
    def get_road_data(left_lat, left_lng, right_lat, right_lng, region):
        overpass_url = "http://overpass-api.de/api/interpreter"
        overpass_query = f"""
        [out:json];
        (
          way["highway"~"footway|pedestrian|path|living_street|residential|track|service"]({right_lat},{left_lng},{left_lat},{right_lng});
        );
        out body;
        >;
        out skel qt;
        """
        response = requests.get(overpass_url, params={'data': overpass_query})

        if response.status_code == 200:
            data = response.json()
            return data, region
        else:
            print(f"에러코드: {response.status_code}")
            return None, region

    # 도로 데이터 처리 함수
    def process_road_data(data, region_name):
        nodes = {}
        ways = {}
        rows = []

        if data:
            for element in data['elements']:
                if element['type'] == 'node':
                    nodes[element['id']] = (element['lat'], element['lon'])
                elif element['type'] == 'way':
                    ways[element['id']] = element['nodes']

            for way_id, node_ids in ways.items():
                for node_id in node_ids:
                    if node_id in nodes:
                        lat, lng = nodes[node_id]
                        rows.append({
                            "way_id": str(way_id),
                            "node_id": str(node_id),
                            "lat": lat,
                            "lng": lng,
                            "region": region_name
                        })
        schema = types.StructType([
            types.StructField("way_id", types.StringType(), True),
            types.StructField("node_id", types.StringType(), True),
            types.StructField("lat", types.DoubleType(), True),
            types.StructField("lng", types.DoubleType(), True),
            types.StructField('region', types.StringType(), True)
        ])
        # Spark DataFrame으로 변환
        df = spark.createDataFrame(rows, schema=schema)
        return df

    # 도로 데이터 가져오기
    road_data, region = get_road_data(left_lat, left_lng, right_lat, right_lng, region)

    # 도로 데이터를 Spark DataFrame으로 반환받기
    road_df = process_road_data(road_data, region)
    pedestrian_df = road_df.dropDuplicates()
    # pedestrian_df.write.option("header", "true").csv(f"./pedestrian_{region}")

    return pedestrian_df
