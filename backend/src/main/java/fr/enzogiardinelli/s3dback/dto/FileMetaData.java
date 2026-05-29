package fr.enzogiardinelli.s3dback.dto;

import java.time.Instant;

public record FileMetaData(String name, long size, Instant dateTime, String contentType) {}
