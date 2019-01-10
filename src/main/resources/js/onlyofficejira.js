JIRA.bind(JIRA.Events.NEW_CONTENT_ADDED, function (e, context, reason) {

    // console.log("============================");
    // console.log(e);
    // console.log(context);
    // console.log(reason);
    // console.log("============================");

    // if (reason === JIRA.CONTENT_ADDED_REASON.panelRefreshed) {
    //     var newContent = context[0];
    //
    //     if (newContent.tagName === "DIV" &amp;&amp; newContent.id.match(/my-id|my-second-id/)) {
    //         AJS.$("#"+newContent.id).replaceWith(newContent.outerHTML);
    //     }
    // }

    var ooAddRow = function (copyElem) {
        //console.log(attElem);

        var rowTemplate = AJS.$("div#attachment_item_template").html();
        //var copyElem = AJS.$(AJS.$("li.attachment-content")[0]);

        // __attachId__
        var attachId = copyElem.find("div.attachment-delete a").attr("id").substring(4);
        // __attachFileName__
        var attachFileName = copyElem.find("a.attachment-title").text();
        // __attachFileSize__
        var attachFileSize = copyElem.find("dd.attachment-size").text();
        // __attachCreated__
        var attachCreated = copyElem.find("dd.attachment-date time").attr("datetime");

        console.log("rowTemplate");
        console.log(rowTemplate);


        console.log("=========== find vars ===========");
        console.log("__attachId__");
        console.log(attachId);
        console.log("__attachFileName__");
        console.log(attachFileName);
        console.log("__attachFileSize__");
        console.log(attachFileSize);
        console.log("__attachCreated__");
        console.log(attachCreated);

        rowTemplate = rowTemplate.replace(/__attachId__/g, attachId);
        rowTemplate = rowTemplate.replace(/__attachFileName__/g, attachFileName);
        rowTemplate = rowTemplate.replace(/__attachFileSize__/g, attachFileSize);
        rowTemplate = rowTemplate.replace(/__attachCreated__/g, attachCreated);

        console.log("rowTemplate with replace");
        console.log(rowTemplate);

        AJS.$("ol#oo_file_attachments").append(rowTemplate);

        return true;
    }

    // обновление нашего блока
    if (reason === JIRA.CONTENT_ADDED_REASON.panelRefreshed) {
        var refContent = context[0];

        console.log(" ++ ============================");
        // console.log(refContent);
        // console.log(refContent.tagName);
        // console.log(refContent.id.substring(0, 10));
        // console.log(" ++ ============================");

        if ((refContent.tagName === "OL") && (refContent.id.substring(0, 10) == "attachment")) {
            // тут надо обходить весь блок вложении чтобы переформировать наш блок
            //AJS.$("ol#refContent.id li")
            //console.log("===123===");

            // 1 - пробегаемся по идентификаторам вложении и сравниваем их с нашими вложениями
            // то есть подготавливаем массив идентификаторов наших вложений
            var ooAttArr =[];
            var arrObj = AJS.$("ol#oo_file_attachments li");
            var arrSize = arrObj.size();
            for (var i = 0; i < arrSize; i++) {
                ooAttArr[i] = AJS.$(arrObj[i]).attr("data-attachment-id");
            }
            console.log(ooAttArr);

            // 2 - пробегаемся по id вложений и сравниваем что есть в нашем массиве
            // arrObj = AJS.$("ol#" + refContent.id + " li");
            arrObj = AJS.$("ol#" + refContent.id + " li .attachment-delete a");
            arrSize = arrObj.size();
            var delId = "";
            var attId = "";
            for (var i = 0; i < arrSize; i++) {
                delId = AJS.$(arrObj[i]).attr("id");
                attId = delId.substring(4);
                if (ooAttArr.indexOf(attId) == -1) {
                    console.log("добавлен id " + attId);
                    // тут получается что найдено добавленное вложение, надо бы его также добавить в нашу панель
                    // добавлять будем через функцию
                    ooAddRow(AJS.$(AJS.$("ol#" + refContent.id + " li")[i]));
                }
            }
        }
    }
});


AJS.$(function() {
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
    // AJS.$(document).on("click", "#demo-warning-dialog button", function (e) {
    //     e.preventDefault();
    //     AJS.dialog2("#demo-warning-dialog").hide();
    // });

    AJS.$("div button.aui-button").click(function (e) {
        e.preventDefault();
        AJS.$("#demo-warning-dialog").attr("attachid", AJS.$(this).attr("attachId"));
        AJS.dialog2("#demo-warning-dialog").show();
    });


    AJS.$(document).on("click", "#demo-warning-dialog button", function (e) {
        e.preventDefault();
        AJS.dialog2("#demo-warning-dialog").hide();

        var rest_url = AJS.params.baseURL + "/rest/api/2/attachment/" + AJS.$("#demo-warning-dialog").attr("attachId");

        if (AJS.$(this).attr("id") == "warning-dialog-confirm") {

            AJS.$.ajax({
                // url:  AJS.params.baseURL + "/rest/api/2/attachment/" + AJS.$(this).attr("attachId"),
                url:  rest_url,
                type: 'DELETE',
                success: function(result) {
                    // удалим строку
                    // console.log("=============");
                    console.log("удачно");
                    // console.log(AJS.$(element).parent().parent());
                    // AJS.$(element).parent().parent().remove();
                },
                error: function(e) {
                    console.log("ошибка");
                    // Do something with the result
                    // AJS.messages.error("#aui-message-bar", {
                    //     title: 'Ошибка',
                    //     body: e
                    // });
                }
            });




            console.log("удаление");
        }


    })


})