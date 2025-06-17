// File: functions/index.js

const functions = require("firebase-functions");
const stripe = require("stripe")(functions.config().stripe.secret);

exports.createStripePaymentIntent = functions.https.onCall(
    async (data, context) => {
      // Dữ liệu đầu vào: amount và currency
      const amount = data.amount;
      const currency = "vnd";

      // Ghi log để debug trên Firebase Console
      functions.logger.info(
          `Creating PaymentIntent for amount ${amount} ${currency}`,
          {structuredData: true},
      );

      try {
        // Yêu cầu Stripe tạo một PaymentIntent
        const paymentIntent = await stripe.paymentIntents.create({
          amount: amount,
          currency: currency,
          automatic_payment_methods: {
            enabled: true,
          },
        });

        // Trả về client_secret cho ứng dụng Android
        return {
          clientSecret: paymentIntent.client_secret,
        };
      } catch (error) {
        // Ghi log lỗi chi tiết
        functions.logger.error("Error creating PaymentIntent:", error);
        // Ném ra một lỗi chuẩn của Functions để client có thể bắt được
        throw new functions.https.HttpsError(
            "internal",
            "Unable to create a payment intent.",
            error,
        );
      }
    },
);