package com.vincentrungogh.global.service;

import com.vincentrungogh.domain.route.service.dto.request.ArtRouteRequestDto;
import com.vincentrungogh.domain.route.service.dto.request.DataSaveRouteRequestDto;
import com.vincentrungogh.domain.route.service.dto.response.DataArtRouteResponseDto;
import com.vincentrungogh.domain.route.service.dto.response.DataSaveRouteResponseDto;
import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import com.vincentrungogh.global.util.ResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class PythonApiService {
    private final RestTemplate restTemplate;
    @Value("${data.api.url}")
    private String dataUrl;

    public DataArtRouteResponseDto convertArtRoute(ArtRouteRequestDto requestDto) {
        URI uri = UriComponentsBuilder
                .fromUriString(dataUrl)
                .path("/api/v1/rootings/art")
                .encode()
                .build()
                .toUri();

        //제네릭 타입을 명확히 지정
        //제네릭타입은 런타임 시 LinkedHashMap와 같은 일반적인 타입으로 변경
        //이를 해결하기 위해 타입을 정확하게 지정
        ResponseEntity<ResultDto<DataArtRouteResponseDto>> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(requestDto),
                new ParameterizedTypeReference<ResultDto<DataArtRouteResponseDto>>() {}
        );

        log.info(response.toString());

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new CustomException(ErrorCode.FAILED_CHANGE_ROOTING);
        }

        return (DataArtRouteResponseDto) response.getBody().getData();
    }

    public DataSaveRouteResponseDto saveRoute(DataSaveRouteRequestDto requestDto) {
        URI uri = UriComponentsBuilder
                .fromUriString(dataUrl)
                .path("/api/v1/rootings")
                .encode()
                .build()
                .toUri();

        //제네릭 타입을 명확히 지정
        //제네릭타입은 런타임 시 LinkedHashMap와 같은 일반적인 타입으로 변경
        //이를 해결하기 위해 타입을 정확하게 지정
        ResponseEntity<ResultDto<DataSaveRouteResponseDto>> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(requestDto),
                new ParameterizedTypeReference<ResultDto<DataSaveRouteResponseDto>>() {}
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new CustomException(ErrorCode.FAILED_SAVE_ROOTING);
        }

        return (DataSaveRouteResponseDto) response.getBody().getData();
    }
}

