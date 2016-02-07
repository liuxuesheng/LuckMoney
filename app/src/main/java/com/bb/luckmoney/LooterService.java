package com.bb.luckmoney;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class LooterService extends AccessibilityService {
    private AccessibilityNodeInfo mRootNodeInfo = null;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //get whole data from accessibility event
        mRootNodeInfo = event.getSource();
        if (mRootNodeInfo == null) {
            return;
        }

        //Open chat window
/*        try {
            openChatWindow(event);
        } catch (Exception ex) {

        }*/

        //Open lucky money
        openLuckyMoney(event);

        //Accept lucky money
        acceptLuckyMoney(event);

        // Exit lucky money
        exitLuckyMoney(event);

    }

    @Override
    public void onInterrupt() {

    }

    private void openChatWindow(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            List<AccessibilityNodeInfo> chatContent = mRootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c8");
            AccessibilityNodeInfo theChatMessage = null;
            int numOfChats = chatContent.size();

            if (numOfChats > 0) {
                for (int i = 0; i < numOfChats - 1; i++) {
                    theChatMessage = chatContent.get(i);
                    String text = theChatMessage.getContentDescription().toString();
                    System.out.println("^^^^^ TEXT: " + text);
                    if (text.contains("[微信红包]")) {
                        theChatMessage.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                }
            }
        }
    }

    private void openLuckyMoney(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            List<AccessibilityNodeInfo> luckyMoney = mRootNodeInfo.findAccessibilityNodeInfosByText("微信红包");

            System.out.println("^^^^^ A");

            if (luckyMoney.size() <= 0) {
                return;
            }

            System.out.println("^^^^^ B");

            AccessibilityNodeInfo theConfirmationMessage = null;
            AccessibilityNodeInfo lastLuckyMoney = null;
            int confirmationY = 0;
            int luckyMoneyY = 0;

            List<AccessibilityNodeInfo> confirmationMessage = mRootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/be");
            int numOfConfirmationMessages = confirmationMessage.size();

            if (numOfConfirmationMessages > 0) {
                for (int i = numOfConfirmationMessages - 1; i > 0; i--) {
                    theConfirmationMessage = confirmationMessage.get(i);
                    String text = theConfirmationMessage.getText().toString();
                    if (text.contains("你领取了") || text.contains("You've opened the Red Packet of")) {
                        Rect confirmationMessageArea = new Rect();
                        theConfirmationMessage.getBoundsInScreen(confirmationMessageArea);
                        confirmationY = confirmationMessageArea.centerY();
                        System.out.println("^^^^^^^^^^^^^^^^^^^^^^: confirmationY: " + confirmationY);
                        break;
                    }
                }
            }

            System.out.println("^^^^^ C");

            if (luckyMoney.size() > 0) {
                lastLuckyMoney = luckyMoney.get(luckyMoney.size() - 1);
                Rect luckyMoneyArea = new Rect();
                lastLuckyMoney.getBoundsInScreen(luckyMoneyArea);
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^: " + luckyMoneyArea.centerX() + " " + luckyMoneyArea.centerY());
                luckyMoneyY = luckyMoneyArea.centerY();
            }

            System.out.println("^^^^^^^^^^^^^^^^^^^^^^: " + confirmationY + " " + luckyMoneyY);

            if (confirmationY > luckyMoneyY) {
                return;
            } else if (lastLuckyMoney != null) {
                lastLuckyMoney.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    private void acceptLuckyMoney(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            List<AccessibilityNodeInfo> openItem = mRootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b43");

            if (openItem.size() > 0) {
                AccessibilityNodeInfo theOpenButton = openItem.get(openItem.size() - 1);
                Rect area = new Rect();
                theOpenButton.getBoundsInScreen(area);
                theOpenButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    private void exitLuckyMoney(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            System.out.println("^^^^^ AA");
            List<AccessibilityNodeInfo> luckyMoneyDetails = mRootNodeInfo.findAccessibilityNodeInfosByText("红包详情");
            List<AccessibilityNodeInfo> openItem = mRootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cdh");

            System.out.println("^^^^^ AB");
            if (luckyMoneyDetails.size() > 0 && openItem.size() > 0) {
                System.out.println("^^^^^ AC");
                AccessibilityNodeInfo theBackButton = openItem.get(openItem.size() - 1);
                Rect area = new Rect();
                theBackButton.getBoundsInScreen(area);
                theBackButton.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                System.out.println("^^^^^ AD");
            }
        }
    }
}
