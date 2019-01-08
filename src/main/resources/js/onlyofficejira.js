JIRA.bind(JIRA.Events.NEW_CONTENT_ADDED, function (e, context, reason) {

    console.log("============================");
    console.log(e);
    console.log(context);
    console.log(reason);
    console.log("============================");

    // if (reason === JIRA.CONTENT_ADDED_REASON.panelRefreshed) {
    //     var newContent = context[0];
    //
    //     if (newContent.tagName === "DIV" &amp;&amp; newContent.id.match(/my-id|my-second-id/)) {
    //         AJS.$("#"+newContent.id).replaceWith(newContent.outerHTML);
    //     }
    // }
});
