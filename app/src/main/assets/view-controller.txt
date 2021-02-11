//
//  ViewController.swift
//
//  Tiklal
//
//  Created by Issahar Weiss on 13/01/2021.
//

import WebKit

class ViewController: UIViewController, WKNavigationDelegate, WKUIDelegate {

    var webView: WKWebView!
    
    override func loadView() {
        webView = WKWebView()
        webView.navigationDelegate = self
        view = webView
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let webPagePreferences = WKWebpagePreferences()
        webPagePreferences.allowsContentJavaScript = true
        let configuration = WKWebViewConfiguration()
        configuration.defaultWebpagePreferences = webPagePreferences
        let webView = WKWebView(frame: view.bounds, configuration: configuration)
        let htmlUrl = buildUrl()
        webView.loadFileURL(htmlUrl, allowingReadAccessTo: htmlUrl)
        webView.navigationDelegate = self
        webView.uiDelegate = self
        webView.scrollView.bounces = false
        webView.scrollView.contentInsetAdjustmentBehavior = .never
        view = webView
    }
    
    func buildUrl() -> URL {
        let htmlPath = Bundle.main.path(forResource: "web/index", ofType: "html")
        let htmlUrl = URL(fileURLWithPath: htmlPath!, isDirectory: false)
        var searchString = ""
        if UIDevice.current.userInterfaceIdiom == .pad {
            searchString = "?ipad=true"
        }
        else {
            searchString = "?ipad=false"
        }
        if UserDefaults.standard.bool(forKey: "darkMode") {
            searchString += "&darkMode=true"
        }
        if UserDefaults.standard.bool(forKey: "oldColors") {
            searchString += "&oldColors=true"
        }
        if UserDefaults.standard.integer(forKey: "fontSize") != 0 {
            let fontSize = UserDefaults.standard.integer(forKey: "fontSize")
            searchString += "&fontSize=\(fontSize)"
        }

        return URL(string: searchString, relativeTo: htmlUrl)!;

    }

    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        let url = navigationAction.request.url
        guard url != nil else {
            decisionHandler(.allow)
            return
        }

        if url!.description.lowercased().contains("nosachteiman") || url!.scheme == "tel" || url!.scheme == "mailto" {
            decisionHandler(.cancel)
            UIApplication.shared.open(url!, options: [:])
        }
        else {
            decisionHandler(.allow)
        }
    }
    
    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
        if (navigationAction.request.url != nil) {
            updateUserSettings(url: navigationAction.request.url!)
        }
        return nil
    }
    
    func updateUserSettings(url: URL) {
        let fontSize = getQueryStringParameter(url: url, param: "fontSize")
        let darkMode = getQueryStringParameter(url: url, param: "darkMode")
        let oldColors = getQueryStringParameter(url: url, param: "oldColors")
        if fontSize != nil {
            UserDefaults.standard.set(Int(fontSize!), forKey: "fontSize")
        }
        if darkMode != nil {
            UserDefaults.standard.set(Bool(darkMode!), forKey: "darkMode")
        }
        if oldColors != nil {
            UserDefaults.standard.set(Bool(oldColors!), forKey: "oldColors")
        }
    }
    
    func getQueryStringParameter(url: URL, param: String) -> String? {
      guard let url = URLComponents(url: url, resolvingAgainstBaseURL: false) else { return nil }
      return url.queryItems?.first(where: { $0.name == param })?.value
    }

    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        setNeedsUpdateOfHomeIndicatorAutoHidden()
    }
    
    override var prefersHomeIndicatorAutoHidden: Bool {
        return true
    }
}
