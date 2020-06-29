package choi.security.keystroke.api;

/**
 * Created by Nate on 2017-11-14.
 */


public class APIConst {
    // 공통
    // activity가 요청
    public static final String ARReq = "am_request";

    // activity가 반환하는 결과 bundle의 이름
    public static final String Bundle_AMResult = "bundle_amresult";

    // 처리 중 내부 에러가 발생한지 여부를 나타내는 Bundle의 키
    public static final String Time = "time";

    // 처리 중 내부 에러가 발생한지 여부를 나타내는 Bundle의 키
    public static final String InternalError = "internalerror";

    // --------------------
    // History
    // History 정보
    public static final String History_data = "history_data";

    // --------------------
    // 키스트로크
    // 키스트로크 인증 결과
    public static final String Keystroke_Auth_Result = "keystroke_auth_result";

    // 인증 결과 확률
    public static final String Keystroke_Auth_Result_Prob = "keystroke_auth_result_prob";

    // 인증 결과 임계치
    public static final String Keystroke_Auth_Result_Thres = "keystroke_auth_result_thres";

    // 인증 사용자 취소
    public static final String Keystroke_Auth_User_Cancel = "keystroke_auth_user_cancel";

    // PIN 결과
    public static final String PIN_Auth_Result = "pin_auth_result";


    public static final int INTENT_KEYSTROKE = 6;
}
