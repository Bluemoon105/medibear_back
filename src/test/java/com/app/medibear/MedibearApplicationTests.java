package com.app.medibear;

import com.app.medibear.fastapi.service.impl.FastapiServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;

@SpringBootTest
class MedibearApplicationTests {

  @Autowired
  private FastapiServiceImpl fastapiServiceImpl;

  @Test
  void contextLoads() {
  }

  @Test
  public void testFastapiPrediction() {
    String predict = fastapiServiceImpl.getPrediction();
    System.out.println(predict);
  }

  static {
    try {
      Class.forName("");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void TestConnectDB() {
    // ⚠️ JDBC URL 형식 주의!
    // postgresql:// → jdbc:postgresql:// 로 변경해야 함
    String url = "jdbc:postgresql://ep-little-flower-a9xb7jsq-pooler.gwc.azure.neon.tech:5432/neondb?sslmode=require";
    String user = "neondb_owner";
    String password = "npg_4BSozCbeW6fO";

    try (Connection conn = DriverManager.getConnection(url, user, password)) {
      System.out.println("✅ DB 연결 성공!");
      System.out.println("연결 객체: " + conn);
    } catch (Exception e) {
      e.printStackTrace();
      fail("❌ DB 연결 실패: " + e.getMessage());
    }
  }

}
