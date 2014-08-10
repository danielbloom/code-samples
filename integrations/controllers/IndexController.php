<?php 
/** 
* NOTE: all files have been edited and trimmed for pubic display
*/

/**
* Integrations page controller
*
* This controller manages the interface wherein a user can view their current 
* integration status, and can add, allow or revoke integration access with 
* various third party services.
*/
 
class integrations_IndexController extends XXX_Controller_Action_AssetLoader
{
 
    protected $cache_version = 1403286834931;

    /**
    * Get Integrations that have FTP credentials
    *
    * @param int $refIntegrationId
    * @param int $sourcetrackVersion
    * @return XXX_Model_Integrations_CredentialsCollection
    */
    private function getFtpIntegrations($refIntegrationId, $sourcetrackVersion)
    {
        // Get sourcetrack/ftp integrations configured for this account
        $credMapper = new XXX_Model_Mapper_Integrations_Credentials();
        $creds = new XXX_Model_Integrations_Credentials(array(
                      'acct_id' => $GLOBALS['XXX_User']->getAccountID(),
                      'ref_integration_id' => $refIntegrationId
        ));
        $integrationsColl = $credMapper->getSourcetrackIntegrationsForView($creds, $sourcetrackVersion);

        return $integrationsColl;
    }

    /**
    * Get available sourcetrack sets for an integration
    *
    * @param XXX_Model_Integrations_CredentialsCollection $integrations
    * @param array $sourcetrackSets
    * @return array available sourcetrack sets
    */
    private function getSourcetrackAvailable($integrations, $sourcetrackSets)
    {
        foreach ($integrations as $integration) {
            unset($sourcetrackSets[$integration->integration_key]);
        }
        return $sourcetrackSets;
    }

    /**
    * Display main integrations page
    */
    public function indexAction()
    {
        $usrIntegrations = $usrMappear->getIntegrations($usrInts);
        $hasSourcetrack2Access = $GLOBALS['XXX_User']->getAccountItemAccess(XXX);
 
        // Determine if the user has already authorized for Salesforce
        $this->view->salesforce = 'Allow Access';
        $this->view->sourceTrackSets = $stSets;
 
        $this->view->kenshooIntegrations = $this->getFtpIntegrations(2, 1);
        $this->view->sourceTrackAvailable = $this->getSourcetrackAvailable($this->view->kenshooIntegrations, $stSets);

        // some integrations require sourcetrack 2
        $this->view->hasSourcetrack2Access = $hasSourcetrack2Access;

        if ($hasSourcetrack2Access) {
            // Get SourceTrack 2 sets
            $st2Mapper = new XXX_Model_Mapper_Sourcetrack2_DomainSet();
            $st2Mapper->setAttributes(array(
                  "auth"   => array("acct_id" => $GLOBALS['XXX_User']->getAccountID()),
                  "paging" => array("offset" => 0, "limit" => 1000),
                  "sort"   => array("updated_date DESC")
            ));

            $sourcetrack2 = $st2Mapper->getDomainSets(new XXX_Model_Sourcetrack2_DomainSetCollection());

            $st2Sets = array();

            foreach ($sourcetrack2 as $set) {
                $st2Sets[$set->id] = $set->name;
            }
            $this->view->sourceTrack2Sets = $st2Sets;

            // Get marin integrations configured for this account
            $this->view->marinIntegrations = $this->getFtpIntegrations(4, 2);
            $this->view->marinSourceTrack2Available = $this->getSourcetrackAvailable($this->view->marinIntegrations, $st2Sets);
         }
    }
}
 
