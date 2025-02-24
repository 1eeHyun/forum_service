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

// Insert image after uploading
document.getElementById('imageFile').addEventListener('change', function () {
    const file = this.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('imageFile', file);

    fetch('/community/upload-image', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.url) {
            insertImageAtCursor(data.url);
        } else {
            alert('Image upload failed: ' + data.error);
        }
    })
      .catch(error => {
          console.error('Upload failed:', error);
      });
});

// Upload Image
function insertImageAtCursor(imageUrl) {
    const contentField = document.getElementById('body');

    // Create image element
    const imgElement = document.createElement('img');
    imgElement.src = imageUrl;
    imgElement.alt = 'Uploaded Image';
    imgElement.style.maxWidth = '100%';
    imgElement.style.display = 'block';
    imgElement.style.margin = '10px 0';

    // Insert Image
    const selection = window.getSelection();
    if (!selection || selection.rangeCount === 0) return;

    const range = selection.getRangeAt(0);
    range.insertNode(imgElement);

    range.setStartAfter(imgElement);
    range.setEndAfter(imgElement);
    selection.removeAllRanges();
    selection.addRange(range);

    contentField.focus();
}
