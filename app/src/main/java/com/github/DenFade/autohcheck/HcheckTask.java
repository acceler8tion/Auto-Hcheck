package com.github.DenFade.autohcheck;

import com.github.DenFade.autohcheck.covid.HcheckClient;
import com.github.DenFade.autohcheck.covid.HcheckRequest;
import com.github.DenFade.autohcheck.exception.AuthorizeHcheckException;
import com.github.DenFade.autohcheck.exception.EndHcheckException;
import com.github.DenFade.autohcheck.exception.EnterHcheckException;
import com.github.DenFade.autohcheck.exception.SubmitHcheckException;

import java.util.TimerTask;

public class HcheckTask extends TimerTask {

    private String schoolCode;
    private String schoolName;
    private String realName;
    private String birth;
    private String edu;

    HcheckTask(String schoolCode, String schoolName, String realName, String birth, String edu){
        this.schoolCode = schoolCode;
        this.schoolName = schoolName;
        this.realName = realName;
        this.birth = birth;
        this.edu = edu;
    }

    @Override
    public void run() {
        try{
            HcheckClient client = new HcheckClient(schoolCode, schoolName, realName, birth, edu);
            HcheckRequest request = new HcheckRequest(client);
            request.enter()
                    .authorize()
                    .submit()
                    .end();
        } catch (EndHcheckException e) {
            e.printStackTrace();
        } catch (EnterHcheckException e) {
            e.printStackTrace();
        } catch (AuthorizeHcheckException e) {
            e.printStackTrace();
        } catch (SubmitHcheckException e) {
            e.printStackTrace();
        }
    }

}
