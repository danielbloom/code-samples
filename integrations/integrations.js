$(document).ready(function() {
      $('.marinRevoke').click( function() {
          removeMarinIntegration($(this).data('id'));
      });
});

// Adds a new Marin integration by storing Marin FTP credentials and an associated Sourcetrack set
function addMarinIntegration () {
      var sourcetrackId = $('select[name="marinStId"]').val(),
          marinFtpName = $('input[name="marinFtpName"]').val(),
          marinFtpPass = $('input[name="marinFtpPass"]').val(),
          marinTrackerId = $('input[name="marinTrackerId"]').val(),
          $requiredError = $('.marinIntegration .integrationsError'),
          $credsError = $('.marinIntegration .integrationsCredError'),
          $marinAdd = $('input[name="marinAdd"]'),
          $loading = $('.loading');

      $requiredError.hide();
      $credsError.hide();

      // validate requred fields
      if (!marinFtpName || !marinFtpPass || !marinTrackerId) {
          $requiredError.show();
          return;
      }

      $marinAdd.hide();
      $loading.show();

      // save FTP credentials, display error if invalid
      $.ajax({
          url: '/integrations/marin/add',
          data: {
              stId: sourcetrackId,
              name: marinFtpName,
              pass: marinFtpPass,
              trackerId: marinTrackerId
          },
          success: function () {
                  window.location.href = '/integrations';
          },
          statusCode: {
              400: function(response) {
                  $loading.hide();
                  $credsError.text(response.responseText).show();
                  $marinAdd.show();
              },
              500: function() {
                  var errorMsg = "There was an error saving your FTP credentials. Please try again later or contact customer support.";
                  $loading.hide();
                  $credsError.text(errorMsg).show();
                  $marinAdd.show();
              }
          }
      });
}

// Removes existing Marin integration by deleting stored Marin FTP credentials
function removeMarinIntegration(stId) {
      var confirmation = confirm('Are you sure you want to delete this integration?');
      if (confirmation) {
          $.ajax({
              url: '/integrations/marin/revoke',
              data: {
                  stId: stId
              },
              success: function () {
                      window.location.href = '/integrations';
              },
              statusCode: {
                  400: function(response) {
                      $('.integrationsCredError').text(response.responseText).show();
                  },
                  500: function() {
                      var errorMsg = "There was an error revoking your Marin integration. Please try again later or contact customer support.";
                      $('.integrationsCredError').text(errorMsg).show();
                  }
              }
          });
      }
}