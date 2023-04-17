var loadFinished = false
var showFinished = false

function fadeOut() {
    if(loadFinished && showFinished && false) {
        anime({
            targets: '#loader',
            opacity: {
                value: 0,
                easing: 'linear'
            },
            duration: 400
        });
    }
}


$(function(){
    $('#loader .loader-sentence').each(function(i){
        var newHtml = '';
        this.textContent.trim().split('').forEach(function(c){
            newHtml += '<span class="loader-show-word loader-hidden">' + c + '</span>';
        })
        $(this).html(newHtml);
    });

    var showWords = $('#loader .loader-show-word');
    showWords.each(function(i){
        $(this).delay(50 * i).queue(function(){
            $(this).removeClass('loader-hidden').dequeue();
        });
    });
    setTimeout(function(){
        showFinished = true;
        fadeOut();
    }, 50 * (showWords.length + 5));
});

$(window).on('load', function() {
    loadFinished = true;
    fadeOut();
});