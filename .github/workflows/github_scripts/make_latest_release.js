module.exports = async ({github, context, core}) => {
    const {TAG} = process.env

    console.log(TAG);

    var startingRelease = await github.rest.repos.getLatestRelease({
        owner: context.repo.owner,
        repo: context.repo.repo,
    });
    console.log(startingRelease);

    var releaseNotes = await github.rest.repos.generateReleaseNotes({
        owner: context.repo.owner,
        repo: context.repo.repo,
        tag_name: TAG,
        previous_tag_name: startingRelease.data.tag_name
    });
    console.log(releaseNotes);

    var targetRelease = await github.rest.repos.getReleaseByTag({
        owner: context.repo.owner,
        repo: context.repo.repo,
        tag: TAG,
    });
    console.log(targetRelease);

    await github.rest.repos.updateRelease({
        owner: context.repo.owner,
        repo: context.repo.repo,
        release_id: targetRelease.data.id,
        body: releaseNotes.data.body,
        prerelease: false,
        make_latest: true
    });
}
