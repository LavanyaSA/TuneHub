package com.example.demo.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entities.Users;
import com.example.demo.services.UserService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import jakarta.servlet.http.HttpSession;


@Controller
public class PaymentController {
	@Autowired
	UserService service;
	@GetMapping("/pay")
	public String pay() {
		return "pay";
	}
	@GetMapping("/payment-success")
	public String paymentSuccess(HttpSession session) {
		String mail =  (String) session.getAttribute("email");
		Users u = service.getUser(mail);
		u.setPremium(true);
		service.updateUser(u);
		return "customerHome";
	}
	
	@GetMapping("/payment-failure")
	public String paymentFailure() {
		return "customerHome";
	}

	@SuppressWarnings("finally")
	@PostMapping("/createOrder")
	@ResponseBody
	public String createOrder(HttpSession session)
	{
		int amount=5000;
		Order order=null;
		try
		{
			RazorpayClient razorpay=new RazorpayClient("rzp_test_j7g9nKf5Gn0vZ6","A4e8BGlughLiyh53lXCeX5LN");
			JSONObject orderRequest=new JSONObject();
			orderRequest.put("amount", amount*100);
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", "order_rcptod_11");
			
			order=razorpay.orders.create(orderRequest);

		}
		catch (RazorpayException e) {
			e.printStackTrace();
		}
		finally {
			return order.toString();
		}
	}
	@PostMapping("/verify")
	@ResponseBody
	public boolean verifyPayment(@RequestParam  String orderId, @RequestParam String paymentId, @RequestParam String signature) {
	    try {
	        // Initialize Razorpay client with your API key and secret
	        RazorpayClient razorpayClient = new RazorpayClient("rzp_test_j7g9nKf5Gn0vZ6", "A4e8BGlughLiyh53lXCeX5LN");
	        // Create a signature verification data string
	        String verificationData = orderId + "|" + paymentId;

	        // Use Razorpay's utility function to verify the signature
	        boolean isValidSignature = Utils.verifySignature(verificationData, signature, "A4e8BGlughLiyh53lXCeX5LN");

	        return isValidSignature;
	    } catch (RazorpayException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	
}