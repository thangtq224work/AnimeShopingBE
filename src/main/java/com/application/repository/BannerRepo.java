package com.application.repository;


import com.application.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepo extends JpaRepository<Banner, Integer> {
}
