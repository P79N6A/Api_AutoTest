package com.touzhijia.converter;

import com.touzhijia.assertion.Assertion;
import com.touzhijia.domain.dto.RequestDTO;
import com.touzhijia.domain.dto.ResponseDTO;
import com.touzhijia.domain.entity.TestStep;
import com.touzhijia.function.ParametersFactory;
import com.touzhijia.assertion.ResponseChecker;
import com.touzhijia.http.HttpRequestClient;
import com.touzhijia.repository.TestStepRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.touzhijia.domain.entity.TestStep.TestResult;

import java.util.List;
import java.util.Map;


/**
 * Created by chenxl on 2018/4/11.
 */

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class RequestConverterTest {
    @Autowired
    private TestStepRepository testStepRepository;

    @Test
    public void testStepToRequestDTO() throws Exception {
        String baseUrl = "http://a.io.tzj.net/core.bid.svc/";
        TestStep testStep = new TestStep();
        testStep.setStepName("担保人注册");
        testStep.setCaseId(1);
//        testStep.setRequestPath("api/loans/${b_username}");
        testStep.setRequestPath("api/loans");
        testStep.setRequestMethod("post with row");
//        testStep.setRequestParams("{\"type\":\"${b_username}\",\"page\":1}");
        testStep.setRequestBody("{\"uid\":\"${b_username}\",\"amount\":\"10\",\"borrowPeriod\":\"1\",\"borrowPeriodUnit\":\"月\"," +
                "\"rate\":12, \"description\":\"哈哈哈\",\"loanType\":\"PERSON\",\"repaymentType\":\"ONE_TIME\"," +
                "\"title\":\"债权申请\"" +
                "}");
        testStep.setNeedTransfer(true);
        testStep.setNeedVerifyValue(true);
        testStep.setTransferParams("loadId=$.id");


        Map<String, String> parameterMap = ParametersFactory.getParameterMap();
        parameterMap.put("b_username", "mall-LEnLvfiEDA");
        RequestDTO requestDTO = RequestConverter.testStepToRequestDTO(testStep);
        log.info(requestDTO.getUrl());
        log.info(requestDTO.getBody());
//        log.info(requestDTO.getParams().toString());
        HttpRequestClient requestClient = new HttpRequestClient();
        ResponseDTO responseDTO = requestClient.execute(baseUrl, requestDTO);
        testStep.setResponseBody(responseDTO.getBody());
        ParametersFactory.saveCommonParam(testStep);
        log.info(parameterMap.toString());

        TestStep testStep1 = new TestStep();
        testStep1.setRequestPath("api/debts/package");
        testStep1.setRequestMethod("post with row");
        testStep1.setRequestBody("{\"loanId\":\"${loadId}\",\"categoryId\":8}");
        RequestDTO requestDT01 = RequestConverter.testStepToRequestDTO(testStep1);
        HttpRequestClient requestClient01 = new HttpRequestClient();
        ResponseDTO responseDTO01 = requestClient01.execute(baseUrl, requestDT01);
        log.info(responseDTO01.toString());

    }

    @Test
    public void testExecute() throws Exception {
        String baseUrl = "http://a.io.tzj.net/";
        List<TestStep> testSteps = testStepRepository.findAll();
        for (TestStep testStep : testSteps) {
            try {
                RequestDTO requestDTO = RequestConverter.testStepToRequestDTO(testStep);
                HttpRequestClient requestClient = new HttpRequestClient();
                ResponseDTO responseDTO = requestClient.execute(baseUrl, requestDTO);
                testStep.setResponseBody(responseDTO.getBody());
                testStep.setTestResult(TestResult.PASS);
            } catch (RuntimeException e) {
                log.info("测试失败:{}", testStep.getStepName());
                testStep.setTestResult(TestResult.False);
            }
            testStepRepository.save(testStep);
            ParametersFactory.saveCommonParam(testStep);
            log.info(ParametersFactory.getParameterMap().toString());
        }
    }

    @Test
    public void testCheck01() throws Exception {
        String baseUrl = "http://a.io.tzj.net/";
        TestStep testStep = testStepRepository.findOne(1);

        try {
            RequestDTO requestDTO = RequestConverter.testStepToRequestDTO(testStep);
            HttpRequestClient httpRequestClient = new HttpRequestClient();
            ResponseDTO responseDTO = httpRequestClient.execute(baseUrl, requestDTO);
            testStep.setResponseBody(responseDTO.getBody());
//            ResponseChecker responseChecker = new ResponseChecker();
//            boolean checkValue = responseChecker.checkValue(testStep.getCheckString(), responseDTO.getBody());
            if (Assertion.assertEquals(testStep)) {
                log.info("【测试成功】");
                testStep.setTestResult(TestResult.PASS);
            } else {
                log.info("【测试失败】");
                testStep.setTestResult(TestResult.False);
            }

        } catch (RuntimeException e) {
            log.info("【测试失败】:{}", testStep.getStepName());
            testStep.setTestResult(TestResult.False);
        }
        testStepRepository.save(testStep);
        ParametersFactory.saveCommonParam(testStep);
        log.info(ParametersFactory.getParameterMap().toString());
    }

    @Test
    public void testCheck02() throws Exception {
        String baseUrl = "http://a.io.tzj.net/";
        List<TestStep> testSteps = testStepRepository.findAll();
        for (int i = 0; i<testSteps.size(); i++) {
            try {
                RequestDTO requestDTO = RequestConverter.testStepToRequestDTO(testSteps.get(i));
                HttpRequestClient httpRequestClient = new HttpRequestClient();
                ResponseDTO responseDTO = httpRequestClient.execute(baseUrl, requestDTO);
                testSteps.get(i).setResponseBody(responseDTO.getBody());
                boolean result = Assertion.assertEquals(testSteps.get(i));
                if (result) {
                    log.info("【步骤" + (i+1) + "测试成功】");
                    testSteps.get(i).setTestResult(TestResult.PASS);
                } else {
                    log.info("【步骤" + (i+1) + "测试失败】");
                    testSteps.get(i).setTestResult(TestResult.False);
                }

            } catch (RuntimeException e) {
                log.info("【步骤" + (i+1) + "测试失败】:{}", testSteps.get(i).getStepName());
                testSteps.get(i).setTestResult(TestResult.False);
            }
            testStepRepository.save(testSteps.get(i));
            ParametersFactory.saveCommonParam(testSteps.get(i));
            log.info(ParametersFactory.getParameterMap().toString());
        }
    }
}
