var loadFinished = false
var showFinished = false

function fadeOut() {
    if(loadFinished && showFinished) {
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

    $('#loader > .show-first .loader-show-word').each(function(i) {
        $(this).removeClass("loader-first-word");
        $(this).addClass("loader-first-word");
    });

    var firstWords = $('#loader .loader-first-word')
    firstWords.each(function(i){
       $(this).delay(80 * i).queue(function(){
           $(this).removeClass('loader-hidden').dequeue();
       });
    });

    setTimeout(function(){
        var showWords = $('#loader .loader-show-word');
        showWords.each(function(i){
            $(this).delay(3 * i).queue(function(){
                $(this).removeClass('loader-hidden').dequeue();
            });
        });
        setTimeout(function(){
            showFinished = true;
            fadeOut();
        }, showWords.length * 3 - 120);
    }, firstWords.length * 80 + 400);
});

$(window).on('load', function() {
    loadFinished = true;
    fadeOut();
});