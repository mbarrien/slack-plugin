package jenkins.plugins.slack;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.tasks.Publisher;

import java.util.Map;
import java.util.logging.Logger;

@Extension
public class SlackListener extends RunListener<Run<?, ?>> {

    private static final Logger logger = Logger.getLogger(SlackListener.class.getName());

    public SlackListener() {
        super((Class<Run<?, ?>>)(Class<?>)Run.class);
    }

    @Override
    public void onCompleted(Run<?, ?> r, TaskListener listener) {
        getNotifier(r, listener).completed(r);
        super.onCompleted(r, listener);
    }

    @Override
    public void onStarted(Run<?, ?> r, TaskListener listener) {
        // getNotifier(r).started(r);
        // super.onStarted(r, listener);
    }

    @Override
    public void onDeleted(Run<?, ?> r) {
        // getNotifier(r).deleted(r);
        // super.onDeleted(r);
    }

    @Override
    public void onFinalized(Run<?, ?> r) {
        // getNotifier(r).finalized(r);
        // super.onFinalized(r);
    }

    @SuppressWarnings("unchecked")
    FineGrainedNotifier getNotifier(Run<?, ?> run, TaskListener listener) {
        if (run instanceof AbstractBuild) {
            AbstractBuild<?, ?> build = (AbstractBuild<?, ?>)run;
            AbstractProject<?, ?> project = build.getProject();
            Map<Descriptor<Publisher>, Publisher> map = project.getPublishersList().toMap();
            for (Publisher publisher : map.values()) {
                if (publisher instanceof SlackNotifier) {
                    return new ActiveNotifier((SlackNotifier) publisher, listener);
                }
            }
        }
        return new DisabledNotifier();
    }

}
