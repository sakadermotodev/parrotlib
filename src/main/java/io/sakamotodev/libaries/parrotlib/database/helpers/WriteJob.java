package io.sakamotodev.libaries.parrotlib.database.helpers;

import java.util.UUID;

public record WriteJob<V>(UUID uuid, V data) {}
