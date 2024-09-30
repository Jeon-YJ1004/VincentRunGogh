def calculate_time(before_time, after_time):

    before_time_list = before_time.split(":")
    after_time_list = after_time.split(":")
    after_seconds, before_seconds = 0,0

    for i in range(3):
        seconds_unit = 60**(2-i)
        after_seconds += seconds_unit*int(after_time_list[i])
        before_seconds += seconds_unit*int(before_time_list[i])
    time = after_seconds - before_seconds

    if time < 0:
        time+= 60*60*24
    # 만약 다음 time이 12초 이상인 경우 일시정지로 간주
    # print("time: ",time)
    if time >= 12:
        return 0
    return time