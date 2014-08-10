<?php
/**
*   Note: Code has been edited for public display
*/
/**
 * Marin integration controller
 *
 * This resource manages the integration between XXXX and Marin.
 * A user enters Marin FTP credentials which are then tied to a specific YYYYY 2 set.
 * A nightly Zend job then uploads YYYYY data to Marin using those saved credentials.
 * (see MarinLauncherController.php)
 */

class integrations_MarinController extends Zend_Controller_Action
{
        public function init()
      {
          $session = new Zend_Session_Namespace();

          // send user to login page if no session is detected
          if(!is_object($session->legacy['XXX_User']) || $session->legacy['XXX_User']->getAccountID() == 0) {
              $this->redirectToLogout();
              xxx_trace(Zend_Log::DEBUG, 'no session', __FILE__, __LINE__);
              return;
          }

          $this->_helper->viewRenderer->setNoRender();
          $this->_helper->layout()->disableLayout();
          $this->log = Zend_Controller_Front::getInstance()
              ->getParam('bootstrap')
              ->getPluginResource('logging')
              ->getLog();
      }

      /**
       * Add Marin Integration
       */
      public function addAction()
      {
          $session = new Zend_Session_Namespace();

          // Validate parameters
          try {
              $params = $this->getRequest()->getParams();
              if (empty($params['stId']) || !is_numeric($params['stId'])) {
                  throw new XXX_Integration_Exception_InvalidInformation("Missing or invalid Sourcetrack set id");
              } else if (empty($params['name']) || !preg_match('/^ftp\.XXX@[\w]{1,28}\.marinsoftware\.com$/', $params['name'])) {
                  throw new XXX_Integration_Exception_InvalidInformation("Missing or invalid FTP username");
              } else if (empty($params['pass']) ||  strlen($params['pass']) > 30) {
                  throw new XXX_Integration_Exception_InvalidInformation("Missing or invalid FTP password");
              } else if (empty($params['trackerId']) ||  !preg_match('/^[\da-z]{1,15}$/', $params['trackerId'])) {
                  throw new XXX_Integration_Exception_InvalidInformation("Missing or invalid Tracker Id");
              }

              // Check if the SourceTrack ID submitted belongs to the user
              $stMapper = new XXX_Model_Mapper_Sourcetrack2_DomainSet();
              $stConfig = $stMapper->getDomainSetById($params['stId']);

              if (empty($stConfig->id)) {
                  throw new XXX_Integration_Exception_InvalidInformation("Invalid Sourcetrack set");
              }

              // validate ftp credentials
              $connId = ftp_connect('integration.marinsoftware.com');

              if (!@ftp_login($connId, $params['name'], $params['pass'])) {
                  throw new XXX_Integration_Exception_InvalidInformation("Invalid FTP username or password");
              }
              ftp_close($connId);

          } catch (XXX_Integration_Exception_InvalidInformation $e) {
              $this->getResponse()->setBody($e->getMessage())
                                  ->setHttpResponseCode(400);
              return;
          }


          // store the credentials
          try {
              /* Set the file name we will FTP to the Account name without
              * any special characters or spaces */
              $acctProfile = $session->legacy['XXX_User']->getAccountProfile();
              $fileName = str_replace(' ', '_', preg_replace("/[^A-Za-z0-9]/", '', $acctProfile['nickname']));

              $creds = new XXX_Model_Integrations_Credentials(
                  array(
                      'acct_id'   => $session->legacy['XXX_User']->getAccountID(),
                      'host'      => 'integration.marinsoftware.com',
                      'username'  => trim($params['name']),
                      'password'  => trim($params['pass']),
                      'file_name' => $fileName,
                      'integration_key' => $params['stId'],
                      'ref_integration_id' => 4,
                      'directory_path' => trim($params['trackerId']) . '/revenue-XXX'
                  )
              );
              $this->log->info(print_r($creds, true));

              $creds->encryptCredentials();

              $credMapper = new XXX_Model_Mapper_Integrations_Credentials();
              $newCred = $credMapper->createCredentials($creds);

              if (empty($newCred->usr_integration_transfer_credentials_id)) {
                  throw new Exception("Error saving Marin credentials");
              }
              return;

          } catch (Exception $e) {
              $this->log->err($e->getMessage());
              $this->getResponse()->setHttpResponseCode(500);
              return;
          }
      }

      /**
       * Delete Marin Integration
       */
      public function revokeAction()
      {
          $session = new Zend_Session_Namespace();

          $stId = $this->_getParam('stId');

          try {
              if (empty($stId) || !is_numeric($stId)) {
                  throw new Exception("Error revoking Marin integration: Missing or invalid SourceTrack set id", 400);
              }

              $credMapper = new XXX_Model_Mapper_Integrations_Credentials();
              $cred = new XXX_Model_Integrations_Credentials(
                  array(
                      'usr_integration_transfer_credentials_id' => $stId,
                      'acct_id' => $session->legacy['XXX_User']->getAccountID()
                  )
              );

              $deleted = $credMapper->deleteCredential($cred);

              if ($deleted) {
                  $this->getResponse()->setHttpResponseCode(204);
              } else {
                  throw new Exception("Error revoking Marin integration with SourceTrack set " . $stId, 500);
              }

          } catch (Exception $e) {
              $this->log->err($e->getMessage());
              $this->getResponse()->setBody($e->getMessage())
                                  ->setHttpResponseCode($e->getCode());
              return;
          }
      }

}