
JIRA.bind(JIRA.Events.ISSUE_REFRESHED, function() {

    console.log(" ======= from JIRA.Events.ISSUE_REFRESHED");


    AJS.$("div button.aui-button").click(function (e) {
        e.preventDefault();
        AJS.$("#demo-warning-dialog").attr("attachid", AJS.$(this).attr("attachId"));
        AJS.dialog2("#demo-warning-dialog").show();

        var currentdate = new Date();
        var datetime = "Last Sync: " + currentdate.getDay() + "/"+currentdate.getMonth()
            + "/" + currentdate.getFullYear() + " @ "
            + currentdate.getHours() + ":"
            + currentdate.getMinutes() + ":" + currentdate.getSeconds();

        console.log(" ======= " + datetime);
    });
});


//
// JIRA.bind(JIRA.Events.NEW_CONTENT_ADDED, function (e, context, reason) {
//     if (reason !== JIRA.CONTENT_ADDED_REASON.panelRefreshed) {
//
//         AJS.$("div button.aui-button").click(function (e) {
//             e.preventDefault();
//             AJS.$("#demo-warning-dialog").attr("attachid", AJS.$(this).attr("attachId"));
//             AJS.dialog2("#demo-warning-dialog").show();
//
//             var currentdate = new Date();
//             var datetime = "Last Sync: " + currentdate.getDay() + "/"+currentdate.getMonth()
//                 + "/" + currentdate.getFullYear() + " @ "
//                 + currentdate.getHours() + ":"
//                 + currentdate.getMinutes() + ":" + currentdate.getSeconds();
//
//             console.log(" ======= " + datetime);
//         });
//     };
// });


AJS.toInit(function() {
// AJS.$(function() {
    // AJS.$("#warning-dialog-show-button").click(function (e) {
    // AJS.$("div button.aui-button").click(function (e) {
    //
    //     console.log(this);
    //     console.log(AJS.$(this).attr("attachId"));
    //
    //     AJS.$("#demo-warning-dialog header.aui-dialog2-header h2").text("Удалить вложение: " + AJS.$(this).attr("attachName") + " ?");
    //     AJS.$("#demo-warning-dialog input[name='id']").val(AJS.$(this).attr("projectId"));
    //     AJS.$("#demo-warning-dialog input[name='deleteAttachmentId']").val(AJS.$(this).attr("attachId"));
    //     AJS.$("#demo-warning-dialog input[name='atl_token']").val(AJS.$("meta[name=atlassian-token]").attr("content"));
    //
    //
    //
    //     e.preventDefault();
    //
    //
    //     AJS.dialog2("#demo-warning-dialog").show();
    // });
    //
    AJS.$(document).on("click", "#demo-warning-dialog button", function (e) {
        e.preventDefault();
        AJS.dialog2("#demo-warning-dialog").hide();
    });




    // var cliclOnDelButton = function(e) {
    //     e.preventDefault();
    //     AJS.$("#demo-warning-dialog").attr("attachid", AJS.$(this).attr("attachId"));
    //     AJS.dialog2("#demo-warning-dialog").show();
    // }
    //

    // AJS.$("div button.aui-button").click(function (e) {
    //     e.preventDefault();
    //     AJS.$("#demo-warning-dialog").attr("attachid", AJS.$(this).attr("attachId"));
    //     AJS.dialog2("#demo-warning-dialog").show();
    //
    //     var currentdate = new Date();
    //     var datetime = "Last Sync: " + currentdate.getDay() + "/"+currentdate.getMonth()
    //         + "/" + currentdate.getFullYear() + " @ "
    //         + currentdate.getHours() + ":"
    //         + currentdate.getMinutes() + ":" + currentdate.getSeconds();
    //
    //     console.log(" ======= " + datetime);
    // });

    //
    // AJS.$(document).on("click", "#demo-warning-dialog button", function (e) {
    //     e.preventDefault();
    //     AJS.dialog2("#demo-warning-dialog").hide();
    //
    //     var rest_url = AJS.params.baseURL + "/rest/api/2/attachment/" + AJS.$("#demo-warning-dialog").attr("attachId");
    //
    //     if (AJS.$(this).attr("id") == "warning-dialog-confirm") {
    //
    //         AJS.$.ajax({
    //             // url:  AJS.params.baseURL + "/rest/api/2/attachment/" + AJS.$(this).attr("attachId"),
    //             url:  rest_url,
    //             type: 'DELETE',
    //             success: function(result) {
    //                 // удалим строку
    //                 // console.log("=============");
    //                 console.log("удачно");
    //                 // console.log(AJS.$(element).parent().parent());
    //                 // AJS.$(element).parent().parent().remove();
    //                 console.log(this);
    //                 console.log(AJS.$(this));
    //
    //
    //             },
    //             error: function(e) {
    //                 console.log("ошибка");
    //                 // Do something with the result
    //                 AJS.messages.error("#aui-message-bar", {
    //                     title: 'Ошибка',
    //                     body: e
    //                 });
    //             }
    //         });
    //
    //         console.log("удаление");
    //     }
    //
    //
    // })
    //

})