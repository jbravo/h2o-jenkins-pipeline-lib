package ai.h2o.ci.buildsummary

import ai.h2o.ci.BuildResult

class StagesSummary extends SummaryInfo {
    private static final String ID = 'stages'
    private static final String ICON = 'computer.gif'
    private static final String TITLE = 'Stages Overview'

    public static final String TABLE_STYLE = 'border-collapse: collapse'
    public static final String TD_STYLE = 'vertical-align: middle; border: 1px solid #b1b1b1; padding: 0.3em 1em;'
    public static final String TH_STYLE = 'vertical-align: middle; border: 1px solid #b1b1b1; padding: 0.5em;'

    private final List<StageInfo> stages = []

    protected StagesSummary() {
        super(ID, ICON, false, Integer.MIN_VALUE)
        setTitle(TITLE)
    }

    void addStage(final context, final StageInfo stageInfo) {
        stages.add(stageInfo)
        updateContent(context)
    }

    void stage(final context, final String name, final String stageDirName, final Closure body) {
        addStage(context, new StageInfo(name, stageDirName))
        try {
            setStageDetails(context, name)
            body()
            markStageSuccessful(context, name)
        } catch (Exception e) {
            markStageFailed(context, name)
            throw e
        }
    }

    void markStageSuccessful(final context, final String stageName) {
        final StageInfo stage = setStageResult(stageName, BuildResult.SUCCESS)
        updateContent(context)
    }

    void markStageFailed(final context, final String stageName) {
        final StageInfo stage = setStageResult(stageName, BuildResult.FAILURE)
        updateContent(context)
    }

    void setStageDetails(final context, final String stageName) {
        setStageDetails(context, stageName, context.env.NODE_NAME, context.env.WORKSPACE)
    }

    void setStageDetails(final context, final String stageName, final String nodeName, final String workspacePath) {
        final StageInfo stage = findStageInfoWithNameOrThrow(stageName)
        stage.setNodeName(nodeName)
        stage.setWorkspace(workspacePath)
        updateContent(context)
    }

    private StageInfo findStageInfoWithName(final String stageName) {
        return stages.find({ it.getName() == stageName })
    }

    private StageInfo findStageInfoWithNameOrThrow(final String stageName) {
        final StageInfo summary = findStageInfoWithName(stageName)
        if (summary == null) {
            throw new IllegalArgumentException('Cannot find StageSummary with name %s'.format(stageName))
        }
        return summary
    }

    private void setStageResult(final String stageName, final BuildResult result) {
        final StageInfo summary = findStageInfoWithNameOrThrow(stageName)
        summary.setResult(result)
    }

    private void updateContent(final context) {
        setContent(buildContent(context))
        publishAutomatically(context)
    }

    private String buildContent(final context) {
        String stagesTableBody = ''

        for (stageSummary in stages) {
            final BuildResult result = stageSummary.getResult() ?: BuildResult.PENDING
            stagesTableBody += """
                <tr>
                    <td style="${TD_STYLE}"><img src="${BuildSummaryUtils.imageLink(context, BuildSummaryUtils.stageResultToImageName(result), BuildSummaryUtils.ImageSize.LARGE)}" /></td>
                    <td style="${TD_STYLE}">${stageSummary.getName()}</td>
                    <td style="${TD_STYLE}">${stageSummary.getNodeNameText()}</td>
                    <td style="${TD_STYLE}">${stageSummary.getWorkspaceText()}</td>
                    <td style="${TD_STYLE}">${stageSummary.getArtifactsHTML(context)}</td>
                </tr>
            """
        }

        return """
            <table style="${TABLE_STYLE}">
                <thead>
                <tr>
                    <th style="${TH_STYLE}"></th>
                    <th style="${TH_STYLE}">Name</th>
                    <th style="${TH_STYLE}">Node</th>
                    <th style="${TH_STYLE}">Workspace</th>
                    <th style="${TH_STYLE}">Artifacts</th>
                </tr>
                </thead>
                <tbody>
                    ${stagesTableBody}
                </tbody>
            </table>
        """
    }

    class StageInfo {
        private final String name
        private final String stageDirName
        private String nodeName
        private String workspace
        private BuildResult result

        StageInfo(String name, String stageDirName) {
            this.name = name
            this.stageDirName = stageDirName
            this.result = BuildResult.PENDING
        }

        String getName() {
            return name
        }

        String getStageDirName() {
            return stageDirName
        }

        String getNodeName() {
            return nodeName
        }

        String getNodeNameText() {
            return nodeName ?: 'Not yet allocated'
        }

        void setNodeName(String nodeName) {
            this.nodeName = nodeName
        }

        String getWorkspace() {
            return workspace
        }

        String getWorkspaceText() {
            return workspace ?: 'Not yet allocated'
        }

        void setWorkspace(String workspace) {
            this.workspace = workspace
        }

        BuildResult getResult() {
            return result
        }

        void setResult(BuildResult result) {
            this.result = result
        }

        String getArtifactsHTML(final context) {
            if (result == BuildResult.PENDING) {
                return 'Not yet available'
            }
            return "<a href=\"${context.currentBuild.rawBuild.getAbsoluteUrl()}artifact/${stageDirName}/\" target=\"_blank\" style=\"color: black;\">Artifacts</a>"
        }
    }
}