package com.cmsys.linebacker.voip_pjsip;

import android.content.ContentUris;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.cmsys.linebacker.R;

public class SipActivity extends AppCompatActivity {
    protected SipProfile account = null;
    private Button saveButton;
    private String wizardId = "";
    private WizardIface wizard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sip);
        // Setup views
        setupViews();
        // Setup SIP
        setupSip();
    }

    private void setupSip() {

    }

    private void setupViews() {
    }

    /**
     * Save the account with given wizard id
     *
     * @param wizardId the wizard to use for account entry
     */
    private void saveAccount(String wizardId) {
        boolean needRestart = false;

        PreferencesWrapper prefs = new PreferencesWrapper(getApplicationContext());
        account = wizard.buildAccount(account);
        account.wizard = wizardId;
        if (account.id == SipProfile.INVALID_ID) {
//            // This account does not exists yet
//            prefs.startEditing();
//            wizard.setDefaultParams(prefs);
//            prefs.endEditing();
//            applyNewAccountDefault(account);
//            Uri uri = getContentResolver().insert(SipProfile.ACCOUNT_URI, account.getDbContentValues());
//
//            // After insert, add filters for this wizard
//            account.id = ContentUris.parseId(uri);
//            List<Filter> filters = wizard.getDefaultFilters(account);
//            if (filters != null) {
//                for (Filter filter : filters) {
//                    // Ensure the correct id if not done by the wizard
//                    filter.account = (int) account.id;
//                    getContentResolver().insert(SipManager.FILTER_URI, filter.getDbContentValues());
//                }
//            }
//            // Check if we have to restart
//            needRestart = wizard.needRestart();

        } else {
            // TODO : should not be done there but if not we should add an
            // option to re-apply default params
            prefs.startEditing();
            wizard.setDefaultParams(prefs);
            prefs.endEditing();
            getContentResolver().update(ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE, account.id), account.getDbContentValues(), null, null);
        }

        // Mainly if global preferences were changed, we have to restart sip stack
        if (needRestart) {
            Intent intent = new Intent(SipManager.ACTION_SIP_REQUEST_RESTART);
            sendBroadcast(intent);
        }
    }

    public SipProfile buildAccount(SipProfile account) {
        Log.d(THIS_FILE, "begin of save ....");
        account.display_name = accountDisplayName.getText().trim();

        String[] serverParts = accountServer.getText().split(":");
        account.acc_id = "<sip:" + SipUri.encodeUser(accountUserName.getText().trim()) + "@" + serverParts[0].trim() + ">";

        String regUri = "sip:" + accountServer.getText();
        account.reg_uri = regUri;
        account.proxies = new String[]{regUri};


        account.realm = "*";
        account.username = getText(accountUserName).trim();
        account.data = getText(accountPassword);
        account.scheme = SipProfile.CRED_SCHEME_DIGEST;
        account.datatype = SipProfile.CRED_DATA_PLAIN_PASSWD;
        //By default auto transport
        account.transport = SipProfile.TRANSPORT_UDP;
        return account;
    }
}
