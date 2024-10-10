package com.vincentrungogh.domain.drawing.repository;

import com.vincentrungogh.domain.drawing.entity.MongoDrawingDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoDrawingRepository  extends MongoRepository<MongoDrawingDetail, String> {
    List<MongoDrawingDetail> findAllByIdIn(List<String> drawingDetailIds);
}
