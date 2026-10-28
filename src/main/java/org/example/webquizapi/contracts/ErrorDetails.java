package org.example.webquizapi.contracts;

import java.util.Date;

public record ErrorDetails(Date timestamp,
                           String message,
                           String details) {}