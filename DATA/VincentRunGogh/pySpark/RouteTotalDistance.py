from pyspark.sql import SparkSession, Window
from pyspark.sql.functions import col, udf, lag, lead, lit
import math

# 하버사인
def haversine(lat1, lng1, lat2, lng2):
    lat1, lng1, lat2, lng2 = map(math.radians, [lat1, lng1, lat2, lng2])
    dlat = lat2 - lat1
    dlng = lng2 - lng1
    a = math.sin(dlat / 2) ** 2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlng / 2) ** 2
    c = 2 * math.asin(math.sqrt(a))
    distance = int(6371000 * c)
    return distance

# 사용자정의함수 하버사인 함수 등록
haversine_udf = udf(haversine)

def calculate_total_distance(route_coords):
    spark = SparkSession.builder.appName("calc distance in spark").getOrCreate()

    # WARN 로그 추가
    spark.sparkContext.setLogLevel("WARN")

    # Spark DataFrame으로 변환하여 거리 계산
    matched_nodes_df = spark.createDataFrame(route_coords, ["lat", "lng"])

    # 순서대로 처리 window 함수 (pandas shift 기능을 대체함.)
    window_spec = Window.orderBy(lit(1))

    # lag가 이전 좌표, lead가 다음 좌표
    distance_df = matched_nodes_df.withColumn("next_lat", lead("lat").over(window_spec)) \
                                  .withColumn("next_lng", lead("lng").over(window_spec)) \
                                  .filter(col("next_lat").isNotNull()) \
                                  .withColumn("distance", haversine_udf(col("lat"), col("lng"), col("next_lat"), col("next_lng")))

    # 총 거리 계산
    total_distance = distance_df.agg({"distance": "sum"}).collect()[0][0]

    # Spark 종료
    spark.stop()
    return total_distance

