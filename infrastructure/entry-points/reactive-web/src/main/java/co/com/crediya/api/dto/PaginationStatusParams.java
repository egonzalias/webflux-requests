package co.com.crediya.api.dto;

import java.util.List;

public record PaginationStatusParams(List<String> codeStatuses, int page, int size) {
}
