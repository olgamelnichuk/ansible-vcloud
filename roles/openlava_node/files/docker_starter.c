/*
 *  Copyright (C) 2015 David Bigagli
 *
 * * This program is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301, USA
 */

 /* original: https://github.com/openlava/openlava/blob/3.0/examples/docker/docker_starter.c
 */

/* openlava job starter example for the integration with docker.
 */
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <pwd.h>
#include <mntent.h>

static char buf[BUFSIZ];
/*static char volumes[BUFSIZ];*/

char *get_nfs_mounts()
{
  struct mntent *ent;
  FILE *file;

  file = setmntent("/proc/mounts", "r");
  if (file == NULL)
    return NULL;


  char sbuf[512];
  char *s = sbuf;
  int length = 0;

  while (NULL != (ent = getmntent(file))) {
    if (strcmp(ent->mnt_type,"nfs4") == 0)
      s += sprintf(s, " -v %s:%s ", ent->mnt_dir, ent->mnt_dir);/*ent->mnt_fsname);*/
  }
  endmntent(file);

  char * out = malloc(sizeof(sbuf)); 
  sprintf(out, "%s", sbuf);
  return out;     
}

int main(int argc, char **argv)
{
    char *cmd;
    char *image;
    char *options;
    char *username;
    char *jobId;
    FILE *fp;
    FILE *pf;
    time_t t;
    struct tm *tm;
    int cc;
    char buff[512];
    char *mounts;

    fp = fopen("/tmp/docker_starter.log", "a+");
    setbuf(fp, NULL);

    t = time(NULL);
    tm = localtime(&t);

    fprintf(fp, "%d:%d:%d:%d:%d starting\n",
            tm->tm_mon + 1, tm->tm_mday, tm->tm_hour, tm->tm_min, tm->tm_sec);

    /* This is the user command we are going to
     * execute and which is going to run inside
     * docker container
     */
    cmd = argv[1];

    /* This is the image docker will use to create
     * a container in which to run the job
     */
    image = getenv("LSB_DOCKER_IMAGE");
    options = getenv("LSB_DOCKER_OPTIONS");

    struct passwd *p = getpwuid(getuid()); 
    username = p->pw_name;

    /* The jobId is used as the name of the container
     * so job control action can use jobId and use
     * docker command to operate on the container.
     */
    jobId = getenv("LSB_JOBID");

    /* This is the complete command line which is
     * to be run.
     */

    /* mounts = get_nfs_mounts();

    fprintf(fp, "%d:%d:%d:%d:%d mounts: %s\n",
            tm->tm_mon + 1, tm->tm_mday, tm->tm_hour,
            tm->tm_min, tm->tm_sec, mounts);

    */
    sprintf(buf, "sudo dockercmd run --name=%s %s %s %s", jobId, (options != NULL ? options : ""), image, cmd);

    fprintf(fp, "%d:%d:%d:%d:%d starter runs: %s\n",
            tm->tm_mon + 1, tm->tm_mday, tm->tm_hour,
            tm->tm_min, tm->tm_sec, buf);

    /* open a pipe and run the command
     */
    pf = popen(buf, "r");

    /* We don't expect any output from docker
     * so we can just pause in wait4() till
     * the job is done. You can just wait in
     * fgets() if you like.
     */
    while(fgets(buff, sizeof(buff), pf)!=NULL){
	        printf("%s", buff);
	}

    cc = pclose(pf);

    fprintf(fp, "%d:%d:%d:%d:%d run done with status %d\n",
            tm->tm_mon + 1, tm->tm_mday, tm->tm_hour,
            tm->tm_min, tm->tm_sec, cc);
    fclose(fp);

    return 0;
}