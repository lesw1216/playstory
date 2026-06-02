package com.playstory.backend.api.exceljob.excel;

import com.playstory.backend.api.order.model.Order;
import com.playstory.backend.api.order.repository.OrderRepository;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * orders 데이터를 .xlsx로 스트리밍 출력한다.
 * SXSSF(window=100) + keyset 청크 조회로 10만건도 메모리를 묶어 처리하고,
 * window를 넘는 행은 임시 파일로 flush 후 close 시 정리된다.
 */
@Component
@RequiredArgsConstructor
public class OrderExcelWriter {

    private static final int CHUNK_SIZE = 5_000;
    private static final int SXSSF_WINDOW = 100;
    private static final String[] HEADERS = {
        "주문번호", "주문자", "상품명", "카테고리", "결제금액", "주문상태", "주문일시"
    };
    private static final String AMOUNT_FORMAT = "#,##0\"원\"";
    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final OrderRepository orderRepository;

    @Value("${excel.output-dir}")
    private String outputDir;

    /**
     * 주문 전체를 엑셀 파일로 생성하고 저장 경로를 반환한다.
     *
     * @param jobId 파일명에 사용할 job 식별자
     * @return 생성된 .xlsx 파일 경로
     * @throws IOException 파일 쓰기 실패 시
     */
    public Path write(Integer jobId) throws IOException {

        Path target = resolveTargetPath(jobId);

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(SXSSF_WINDOW);
             OutputStream out = Files.newOutputStream(target)) {

            Sheet sheet = workbook.createSheet("orders");
            writeHeader(sheet);
            writeRows(sheet, amountStyle(workbook));

            workbook.write(out);
        }
        // try-with-resources의 close()가 SXSSF 임시 파일까지 정리한다.

        return target;
    }

    private Path resolveTargetPath(Integer jobId) throws IOException {

        Path dir = Path.of(outputDir);
        Files.createDirectories(dir);

        String fileName = "orders-%d-%s.xlsx"
            .formatted(jobId, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        return dir.resolve(fileName);
    }

    private void writeHeader(Sheet sheet) {

        Row header = sheet.createRow(0);

        for (int col = 0; col < HEADERS.length; col++) {
            header.createCell(col).setCellValue(HEADERS[col]);
        }
    }

    /** 결제금액 셀에 적용할 원화 표시 서식. 스타일은 한 번만 만들어 모든 행이 재사용한다. */
    private CellStyle amountStyle(SXSSFWorkbook workbook) {

        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat(AMOUNT_FORMAT));

        return style;
    }

    private void writeRows(Sheet sheet, CellStyle amountStyle) {

        int rowIndex = 1;
        int lastId = 0;
        Pageable chunk = PageRequest.of(0, CHUNK_SIZE);

        while (true) {
            List<Order> orders = orderRepository.findByIdGreaterThanOrderByIdAsc(lastId, chunk);
            if (orders.isEmpty()) {
                break;
            }

            for (Order order : orders) {
                writeRow(sheet.createRow(rowIndex++), order, amountStyle);
            }
            lastId = orders.get(orders.size() - 1).getId();
        }
    }

    private void writeRow(Row row, Order order, CellStyle amountStyle) {

        row.createCell(0).setCellValue(order.getId());
        row.createCell(1).setCellValue(order.getUserName());
        row.createCell(2).setCellValue(order.getProductName());
        row.createCell(3).setCellValue(order.getCategory());

        Cell amountCell = row.createCell(4);
        amountCell.setCellValue(order.getAmount());
        amountCell.setCellStyle(amountStyle);

        row.createCell(5).setCellValue(order.getStatus().name());
        row.createCell(6).setCellValue(order.getOrderDate().format(DATE_FORMAT));
    }
}
