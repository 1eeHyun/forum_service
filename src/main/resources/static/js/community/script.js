function showEditBox(commentId) {

    document.getElementById('comment-content-' + commentId).style.display = 'none';
    document.getElementById('action-buttons-' + commentId).style.display = 'none';
    document.getElementById('edit-box-' + commentId).style.display = 'block';
}

function cancelEdit(commentId) {

    document.getElementById('comment-content-' + commentId).style.display = 'block';
    document.getElementById('action-buttons-' + commentId).style.display = 'flex';
    document.getElementById('edit-box-' + commentId).style.display = 'none';
}

// Reply function toggle
function toggleReplies(commentId) {
    const repliesSection = document.getElementById('replies-' + commentId);
    if (repliesSection.style.display === 'none') {
        repliesSection.style.display = 'block';
    } else {
        repliesSection.style.display = 'none';
    }
}

// Reply Form toggle
function showReplyForm(commentId) {
    const replyForm = document.getElementById('reply-form-' + commentId);
    if (replyForm.style.display === 'none') {
        replyForm.style.display = 'block';
    } else {
        replyForm.style.display = 'none';
    }
}

// Reply edit form toggle
function showEditBox(commentId) {
    const editBox = document.getElementById('edit-box-' + commentId);
    const commentContent = document.getElementById('comment-content-' + commentId);

    editBox.style.display = 'block';
    commentContent.style.display = 'none';
}

function cancelEdit(commentId) {
    const editBox = document.getElementById('edit-box-' + commentId);
    const commentContent = document.getElementById('comment-content-' + commentId);

    editBox.style.display = 'none';
    commentContent.style.display = 'block';
}

function toggleReplyForm(commentId) {
    const replyForm = document.getElementById(`reply-form-${commentId}`);
    if (replyForm.style.display === "none") {
        replyForm.style.display = "block";
    } else {
        replyForm.style.display = "none";
    }
}
