Gatling-like performance testing library.

Aim-
Unlike gatling, have good extensibility and documentation - even to the lowest level.

# How To Contribute
## Bugs, Issues and Feature Requests
If you have discovered a bug, or want a new feature to be included, create an issue on Github under the project repository.
Bugs, and issues (including feature requests) related to extensibility and user-friendliness will be put on highest priority.
While raising an issue, please mention the use-case, steps to reproduce the bug (if it is a bug) and other such information that may help us and the comminuity to understand your issue and resolve it quickly.
Don't forget to put proper labels on your issues as they will help us in filtering and prioritizing your issue.

## Pull Requests
If you want to contribute, you can raise a pull request to merge your work (code or other contribution) to <b>develop</b> branch. The pull requests to merge with <b>master</b> will be rejected for non-priority issues. Before the pull request is merged, it needs to pass certain checks-
<ol>
  <li><b>Approved by a maintainer</b></li>
  <li><b>Security clearance by <a href="https://www.gitguardian.com/">Git Guardian</a></b> : It identifies security issues due to inclusion of information in commits that otherwise should be kept secret (such as passwords and ecryption keys).</li>
  <li><b>Successful Build</b> : The code should build successfully and all the test cases should pass.
  <li><b>Sonar quality gates</b> : When you create a pull request or update your pull request, and the build step completes successfully, a sonar report for your branch is generated at <a href="https://sonarcloud.io/dashboard?id=goodload_goodload">https://sonarcloud.io/dashboard?id=goodload_goodload</a>. At the top-left choose your branch to see the sonar report for your branch. All the quality gates should pass.</li>
  <li><b>Sign Contributor License Agreement (CLA)</b> : We ask you to sign a CLA before we can merge your pull request. You need to do this only the first time you raise a pull request, your acceptance is valid for all your pull requests thereafter. The CLA Assistant Bot will ask you to sign the CLA if you haven't already done so. Note that if we change our CLA at any time, then you might need to read and sign it again.
</ol>

After the pull request has been merged with develop or master branch, the build is run again, along with the sonar quality gates. This time, whole code in the develop or master branch is analyzed by Sonar.
