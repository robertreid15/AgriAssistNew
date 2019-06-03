const functions = require('firebase-functions');
//Admin features of app
const admin = require('firebase-admin');
admin.initializeApp();

exports.triggerToMessages = functions.database.ref('/notifications/{push_id}/{user_id}/')
      .onWrite( (change, context) => {
			//get user id
			const user_id = context.params.user_id;
			if(!change.after.val()){

				return console.log('A Notification has been deleted from the database : ', user_id);


			}
                  //We create a command to getting user name
			const userQuery = admin.database().ref(`all_users/${user_id}/name`).once('value');

                  //We start it async
			return Promise.all([userQuery]).then(result => {

				const userName = result[0].val();


     
		                //create notif data
                              const payload = {
                         	notification: {
                         		title : "New Labour Available",
                         		body: `${userName} has sent request`,
                         		icon: "default",
                         		click_action : "farm_management.notification"
                         	},
                         	data : {
                         		user_id : user_id
                         	}
                              };

     	
                         //send to topic
                         return admin.messaging().sendToTopic("available", payload)
                         		.then(function(response){
                         			console.log('Notification sent successfully:',response);
                         			return null;
                        		}) 
                         		.catch(function(error){
                         			console.log('Notification sent failed:',error);
                         		});


   });

});

