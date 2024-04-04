package com.harian.share.location.closersharelocation.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.harian.share.location.closersharelocation.user.model.Device;

public interface DeviceRepository extends JpaRepository<Device, String> {

}
