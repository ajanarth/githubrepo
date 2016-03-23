buildPipelineView('Environment_Provisioning/ENVIRONMENT_BUILD') {
    title('Create Environment')
    displayedBuilds(1)
    selectedJob('Set_Provisioning_Parameters')
    showPipelineParameters()
    refreshFrequency(60)
	consoleOutputLinkStyle(OutputStyle.NewWindow)
	showPipelineParametersInHeaders()
}