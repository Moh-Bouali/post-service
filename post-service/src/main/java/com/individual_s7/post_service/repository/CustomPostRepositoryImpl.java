package com.individual_s7.post_service.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.individual_s7.post_service.model.IndividualPost;
import com.individual_s7.post_service.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.bson.Document;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void updatePostUsername(Long userId, String newUsername) {
        Query query = new Query(Criteria.where("userId").is(userId));
        Update update = new Update().set("username", newUsername);
        mongoTemplate.updateMulti(query, update, Post.class);
    }

    @Override
    public Page<IndividualPost> findPostsByUserIdInWithLimitedContent(List<Long> friendsIds, Pageable pageable) {
        // Define a default sort if pageable.getSort() is empty
        Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(Sort.Direction.DESC, "createdAt");

        Aggregation aggregation = Aggregation.newAggregation(
                // Match posts from friends
                Aggregation.match(Criteria.where("userId").in(friendsIds)),
                // Unwind the content array to get individual posts
                Aggregation.unwind("content"),
                // Project the necessary fields and exclude the _id field
                Aggregation.project()
                        .and("userId").as("userId")
                        .and("username").as("username")
                        .and("content.content").as("content")
                        .and("content.createdAt").as("createdAt")
                        .andExclude("_id"), // Exclude the _id field
                // Sort the posts
                Aggregation.sort(sort),
                // Facet for pagination and total count
                Aggregation.facet(
                                Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()),
                                Aggregation.limit(pageable.getPageSize())
                        ).as("posts")
                        .and(
                                Aggregation.count().as("total")
                        ).as("totalCount")
        );
        // Execute the aggregation
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "posts", Document.class);
        List<Document> mappedResults = results.getMappedResults();

        List<IndividualPost> posts = new ArrayList<>();
        long total = 0;

        if (!mappedResults.isEmpty()) {
            Document result = mappedResults.get(0);

            // Extract total count
            List<Document> totalCountList = (List<Document>) result.get("totalCount");
            if (totalCountList != null && !totalCountList.isEmpty()) {
                Number totalNumber = (Number) totalCountList.get(0).get("total");
                total = totalNumber.longValue();
            }

            // Extract posts
            List<Document> postsList = (List<Document>) result.get("posts");
            if (postsList != null && !postsList.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                posts = postsList.stream()
                        .map(doc -> mapper.convertValue(doc, IndividualPost.class))
                        .collect(Collectors.toList());
            }
        }
        return new PageImpl<>(posts, pageable, total);
    }
}
