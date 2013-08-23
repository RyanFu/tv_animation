package com.jumplife.ad;

import android.app.Activity;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.mediation.MediationAdRequest;
import com.google.ads.mediation.customevent.CustomEventBanner;
import com.google.ads.mediation.customevent.CustomEventBannerListener;
import com.google.ads.mediation.customevent.CustomEventInterstitial;
import com.google.ads.mediation.customevent.CustomEventInterstitialListener;
import com.vpon.ads.VponAdListener;
import com.vpon.ads.VponAdRequest;
import com.vpon.ads.VponAdRequest.VponErrorCode;
import com.vpon.ads.VponAdSize;
import com.vpon.ads.VponBanner;
import com.vpon.ads.VponInterstitialAd;
import com.vpon.ads.VponPlatform;


//TODO: 這class完整路徑需要註冊在admob mediation web上
public class VponAd implements CustomEventBanner, CustomEventInterstitial {

	private VponBanner vponBanner = null;
	private VponInterstitialAd interstitialAd = null;

	@Override
	public void destroy() {
		if (vponBanner != null) {
			vponBanner.destroy();
			vponBanner = null;
		}
		if (interstitialAd != null) {
			interstitialAd.destroy();
			interstitialAd = null;
		}
	}

	/*
	 * 將admob AdSize轉換成 VponAdSize
	 */
	private VponAdSize getVponAdSizeByAdSize(AdSize adSize) {

		if (adSize.equals(AdSize.BANNER)) {
			return VponAdSize.BANNER;
		} else if (adSize.equals(AdSize.IAB_BANNER)) {
			return VponAdSize.IAB_BANNER;
		} else if (adSize.equals(AdSize.IAB_LEADERBOARD)) {
			return VponAdSize.IAB_LEADERBOARD;
		} else if (adSize.equals(AdSize.IAB_MRECT)) {
			return VponAdSize.IAB_MRECT;
		} else if (adSize.equals(AdSize.IAB_WIDE_SKYSCRAPER)) {
			return VponAdSize.IAB_WIDE_SKYSCRAPER;
		} else if (adSize.equals(AdSize.SMART_BANNER)) {
			return VponAdSize.SMART_BANNER;
		}

		boolean isAutoHeight = false;
		boolean isFullWidth = false;
		if (adSize.isAutoHeight()) {
			isAutoHeight = true;
		}
		if (adSize.isFullWidth()) {
			isFullWidth = true;
		}

		if (isAutoHeight && isFullWidth) {
			return VponAdSize.SMART_BANNER;
		}

		if (isAutoHeight && !isFullWidth) {
			return new VponAdSize(adSize.getWidth(), VponAdSize.AUTO_HEIGHT);
		}
		if (!isAutoHeight && isFullWidth) {
			return new VponAdSize(VponAdSize.FULL_WIDTH, adSize.getHeight());
		}

		if (adSize.isCustomAdSize()) {
			return new VponAdSize(adSize.getWidth(), adSize.getHeight());
		}

		return VponAdSize.SMART_BANNER;
	}

	/*
	 * 將admob的 MediationAdRequest轉換成 VponAdRequest
	 */
	private VponAdRequest getVponAdRequestByMediationAdRequest(MediationAdRequest request) {

		VponAdRequest adRequest = new VponAdRequest();
		if (request.getBirthday() != null) {
			adRequest.setBirthday(request.getBirthday());
		}
		if (request.getAgeInYears() != null) {
			adRequest.setAge(request.getAgeInYears());
		}

		if (request.getKeywords() != null) {
			adRequest.setKeywords(request.getKeywords());
		}

		if (request.getGender() != null) {
			if (request.getGender().equals(AdRequest.Gender.FEMALE)) {
				adRequest.setGender(VponAdRequest.Gender.FEMALE);
			} else if (request.getGender().equals(AdRequest.Gender.MALE)) {
				adRequest.setGender(VponAdRequest.Gender.MALE);
			} else {
				adRequest.setGender(VponAdRequest.Gender.UNKNOWN);
			}
		}

		return adRequest;
	}

	@Override
	public void requestBannerAd(final CustomEventBannerListener listener, final Activity activity, String label, String serverParameter, AdSize adSize,
			MediationAdRequest request, Object customEventExtra) {

		if (vponBanner != null) {
			vponBanner.destroy();
			vponBanner = null;
		}

		VponAdRequest adRequest = getVponAdRequestByMediationAdRequest(request);

		// TODO:請將Vpon的 bannerID 設定在admob的mediation web上 由serverParameeter帶進來
		vponBanner = new VponBanner(activity, serverParameter, getVponAdSizeByAdSize(adSize), VponPlatform.TW);
		
		vponBanner.setAdListener(new VponAdListener() {

			@Override
			public void onVponDismissScreen(com.vpon.ads.VponAd arg0) {
				listener.onDismissScreen();
			}

			@Override
			public void onVponFailedToReceiveAd(com.vpon.ads.VponAd arg0, VponAdRequest.VponErrorCode arg1) {
				listener.onFailedToReceiveAd();
			}

			@Override
			public void onVponLeaveApplication(com.vpon.ads.VponAd arg0) {
				listener.onLeaveApplication();
			}

			@Override
			public void onVponPresentScreen(com.vpon.ads.VponAd arg0) {
				listener.onPresentScreen();
			}

			@Override
			public void onVponReceiveAd(com.vpon.ads.VponAd arg0) {
				listener.onReceivedAd(vponBanner);
			}
		});

		vponBanner.loadAd(adRequest);
	}

	@Override
	public void requestInterstitialAd(final CustomEventInterstitialListener listener, Activity activity, String label, String serverParameter,
			MediationAdRequest request, Object customEventExtra) {

		// TODO:請將Vpon的 interstitialBannerID 設定在admob的mediation web上 由serverParameeter帶進來
		interstitialAd = new VponInterstitialAd(activity, serverParameter, VponPlatform.TW);
		interstitialAd.setAdListener(new VponAdListener() {

			@Override
			public void onVponDismissScreen(com.vpon.ads.VponAd arg0) {
				if (interstitialAd != null) {
					interstitialAd.destroy();
					interstitialAd = null;
				}
				listener.onDismissScreen();
			}

			@Override
			public void onVponFailedToReceiveAd(com.vpon.ads.VponAd arg0, VponAdRequest.VponErrorCode arg1) {
				if (interstitialAd != null) {
					interstitialAd.destroy();
					interstitialAd = null;
				}
				listener.onFailedToReceiveAd();
			}

			@Override
			public void onVponLeaveApplication(com.vpon.ads.VponAd arg0) {
				listener.onLeaveApplication();
			}

			@Override
			public void onVponPresentScreen(com.vpon.ads.VponAd arg0) {
				listener.onPresentScreen();
			}

			@Override
			public void onVponReceiveAd(com.vpon.ads.VponAd arg0) {
				listener.onReceivedAd();
			}

		});
		interstitialAd.loadAd(new VponAdRequest());

	}

	@Override
	public void showInterstitial() {
		if (interstitialAd != null && interstitialAd.isReady()) {
			interstitialAd.show();
		}

	}
}
