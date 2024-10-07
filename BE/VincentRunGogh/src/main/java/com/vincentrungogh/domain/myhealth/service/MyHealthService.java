package com.vincentrungogh.domain.myhealth.service;

import com.vincentrungogh.domain.drawing.entity.EachMonthRouteFreeCount;
import com.vincentrungogh.domain.drawing.entity.EachMonthWalkDistanceTime;
import com.vincentrungogh.domain.drawing.repository.DrawingDetailRepository;
import com.vincentrungogh.domain.drawing.repository.DrawingRepository;
import com.vincentrungogh.domain.myhealth.entity.MyHealth;
import com.vincentrungogh.domain.myhealth.repository.MyHealthRepository;
import com.vincentrungogh.domain.myhealth.service.dto.response.EachMonthMyhealthResponse;
import com.vincentrungogh.domain.user.entity.User;
import com.vincentrungogh.domain.user.repository.UserRepository;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyHealthService {

    // 필요한 의존성 주입
    private final MyHealthRepository myHealthRepository;
    private final UserRepository userRepository;
    private final DrawingRepository drawingRepository;
    private final DrawingDetailRepository drawingDetailRepository;

    // 유저 생성 시 마이헬스 조회
    public MyHealth getMyHealth(User user) {
        return myHealthRepository.findByUser(user).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }

    // 드로잉 활동 정보 조회
    @Transactional
    public EachMonthMyhealthResponse getDrawings(int userId, int year) {

        // 0. EachMonthMyhealthResponse 객체 생성을 위한 field 초기값 세팅
        int[] walkList = new int[12];
        double[] distanceList = new double[12];
        int[] timeList = new int[12];
        int[] completedRouteDrawingList = new int[12];
        int[] completedFreeDrawingList = new int[12];
        int totalWalk = 0;
        double totalDistance = 0.0;
        int totalTime = 0;
        int totalCompletedDrawing = 0;

        // 1. 유저 아이디로 유저 엔티티 특정하기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 2-1. 할당된 유저 엔티티, 받은 연도, 모든 달을 기준으로 QueryDSL
        List<EachMonthWalkDistanceTime> eachMonthWalkDistanceTimes = drawingDetailRepository.findWalkDistanceTimeByYearEachMonth(user, year);
        for(int i = 0; i < eachMonthWalkDistanceTimes.size(); i++) {
            /** 존재하는 달만 총계산과 배열에 꽂기 */
            EachMonthWalkDistanceTime eachMonthWalkDistanceTime = eachMonthWalkDistanceTimes.get(i);
            String beforeParsing = eachMonthWalkDistanceTime.getWhatYearAndMonth(); // "2024-01"
            int monthStep = eachMonthWalkDistanceTime.getMonthStep(); // 월별 총걸음수
            int monthDistance = eachMonthWalkDistanceTime.getMonthDistance(); // 월별 총거리 (단위 : meter)
            double monthKDistance = Math.round((monthDistance / 1000.0) * 100.0) / 100.0; // 월별 총거리 (단위 : km)
            int monthTime = eachMonthWalkDistanceTime.getMonthTime(); // 월별 총시간

            /** 달만 파싱해서 int로 가져오기 */
            String[] parts = beforeParsing.split("-");
            int afterParsing = Integer.parseInt(parts[1]); // 1

            /** 배열 인덱스에 주의하여 데이터 바인딩하기 */
            walkList[afterParsing - 1] = monthStep;
            distanceList[afterParsing - 1] = monthKDistance;
            timeList[afterParsing - 1] = monthTime;

            /** 총계산 누적하기 */
            totalWalk = totalWalk + monthStep;
            totalDistance = totalDistance + monthKDistance;
            totalTime = totalTime + monthTime;
        }

        // 2-2. 할당된 유저 엔티티, 받은 연도, 모든 달을 기준으로 QueryDSL
        List<EachMonthRouteFreeCount> eachMonthRouteFreeCounts = drawingRepository.findRouteFreeCountByYearEachMonth(user, year);
        for(int i = 0; i < eachMonthRouteFreeCounts.size(); i++) {
            /** 존재하는 달만 총계산과 배열에 꽂기 */
            EachMonthRouteFreeCount eachMonthRouteFreeCount = eachMonthRouteFreeCounts.get(i);
            String beforeParsing = eachMonthRouteFreeCount.getWhatYearAndMonth(); // "2024-01"
            int monthRouteDrawingCnt = (int) eachMonthRouteFreeCount.getMonthRouteDrawingCnt(); // 월별 완료된 총 루트드로잉 수
            int monthFreeDrawingCnt = (int) eachMonthRouteFreeCount.getMonthFreeDrawingCnt(); // 월별 완료된 총 자유드로잉 수

            /** 달만 파싱해서 int로 가져오기 */
            String[] parts = beforeParsing.split("-");
            int afterParsing = Integer.parseInt(parts[1]); // 1

            /** 배열 인덱스에 주의하여 데이터 바인딩하기 */
            completedRouteDrawingList[afterParsing - 1] = monthRouteDrawingCnt;
            completedFreeDrawingList[afterParsing - 1] = monthFreeDrawingCnt;

            /** 총계산 누적하기 */
            totalCompletedDrawing = totalCompletedDrawing + monthRouteDrawingCnt + monthFreeDrawingCnt;
        }

        // 3. EachMonthMyhealthResponse 객체 리턴
        return EachMonthMyhealthResponse.createEachMonthMyhealthResponse(walkList, distanceList, timeList, completedRouteDrawingList, completedFreeDrawingList, totalWalk, totalDistance, totalTime, totalCompletedDrawing);
    }
}
