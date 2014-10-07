package fr.grousset.fastsnail.tests

import android.test.AndroidTestCase
import fr.grousset.fastsnail.AndroidUtils

public class AndroidUtilsTests extends AndroidTestCase {

    public void testExtractNameFromMemberWithMPrefix() {
        assert AndroidUtils.extractNameFromMember('mProp') == 'prop'
    }

    public void testExtractNameFromMemberWithoutMPrefix() {
        assert AndroidUtils.extractNameFromMember('prop') == 'prop'
    }

    public void testExtractNameFromMemberWithoutMPrefixButStartingWithM() {
        assert AndroidUtils.extractNameFromMember('member') == 'member'
    }
}